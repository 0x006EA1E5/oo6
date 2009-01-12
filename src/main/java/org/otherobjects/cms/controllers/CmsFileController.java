package org.otherobjects.cms.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.Url;
import org.otherobjects.cms.binding.BindService;
import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.datastore.JackrabbitDataStore;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.model.CmsFile;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.ActionUtils;
import org.otherobjects.cms.util.RequestUtils;
import org.otherobjects.cms.validation.ValidatorService;
import org.otherobjects.cms.views.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller to process form submissions. Only data for types registered in the TypeService is supported.
 *
 * @author rich
 */
@Controller
@SuppressWarnings("unchecked")
public class CmsFileController
{
    private final Logger logger = LoggerFactory.getLogger(CmsFileController.class);

    @Resource
    private JackrabbitDataStore jackrabbitDataStore;

    @Resource
    private TypeService typeService;

    @Resource
    private BindService bindService;

    @Resource
    private ValidatorService validatorService;

    @Resource
    private OoResourceLoader ooResourceLoader;

    @RequestMapping({"/file/save"})
    public ModelAndView save(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) throws IOException
    {
        ActionUtils actionUtils = new ActionUtils(multipartRequest, response, null, null);

        // Prepare object (create or fetch)
        String id = multipartRequest.getParameter("_oo_id");
        String typeName = multipartRequest.getParameter("_oo_type");
        String containerId = multipartRequest.getParameter("_oo_containerId");
        Assert.notNull(typeName, "Type name not specified in _oo_type parameter.");
        TypeDef typeDef = typeService.getType(typeName);
        Assert.notNull(typeDef, "No typeDef found for type: " + typeName);

        CmsFile cmsFile = null;
        GenericJcrDao genericDao = (GenericJcrDao) jackrabbitDataStore.getDao(typeDef);

        if (StringUtils.isNotBlank(id))
        {
            cmsFile = (CmsFile) jackrabbitDataStore.get(id);
        }
        else
        {
            cmsFile = (CmsFile) jackrabbitDataStore.create(typeDef, containerId);
        }

        Assert.notNull(cmsFile, "Could not prepare item for binding: " + typeName);

        // Bind and validate
        BindingResult errors = null;
        errors = bindService.bind(cmsFile, typeDef, multipartRequest);
        Validator validator = validatorService.getValidator(cmsFile);
        if (validator != null)
            validator.validate(cmsFile, errors);
        else
            logger.warn("No validator for item of class " + cmsFile.getClass() + " found");

        // Set fileName so that code can be generated
        if (StringUtils.isBlank(id))
        {
            // FIXME This should be done in the validation
            Assert.notNull(cmsFile.getNewFile(), "A file must be uploaded when creating new images.");
            cmsFile.setOriginalFileName(cmsFile.getNewFile().getOriginalFilename());

            // Check if we have a image with the same name already stored
            int suffix = 2;
            Object existingImage = genericDao.getByPath(cmsFile.getJcrPath());
            String fileStem = StringUtils.substringBeforeLast(cmsFile.getCode(), ".");
            while (existingImage != null)
            {
                cmsFile.setCode(fileStem + "-" + (suffix++) + "." + cmsFile.getExtension());
                existingImage = genericDao.getByPath(cmsFile.getJcrPath());
            }

            // Make sure we always have a label
            if (cmsFile.getLabel() == null)
                cmsFile.setLabel(cmsFile.getCode());

        }

        if (cmsFile.getNewFile() != null)
        {
            // FIXME Merge this with code below
            OoResource resource = ooResourceLoader.getResource("/data/files/" + cmsFile.getCode());
            cmsFile.setMimeType(getMimeType(cmsFile.getCode()).toString());
            cmsFile.setFileSize(resource.getFile().length());
        }
        
        // Save
        boolean success = false;
        if (!(errors != null && errors.hasErrors()))
        {
            // Save new object
            cmsFile = (CmsFile) genericDao.save(cmsFile, false);
            success = true;
        }

        if (cmsFile.getNewFile() != null)
        {

            OoResource resource = ooResourceLoader.getResource("/data/files/" + cmsFile.getCode());
            cmsFile.getNewFile().transferTo(resource.getFile());
        }

        // Prepare return data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("success", success);
        data.put("formObject", cmsFile);
        if (!success)
        {
            List<Object> jsonErrors = new ArrayList<Object>();
            for (FieldError e : (List<FieldError>) errors.getFieldErrors())
            {
                Map<String, String> error = new HashMap<String, String>();
                error.put("id", e.getField());
                //error.put("msg", requestContext.getMessage(e.getCode(), e.getArguments()));
                jsonErrors.add(error);
            }
            data.put("success", false);
            data.put("errors", jsonErrors);
        }

        // Return
        if (RequestUtils.isXhr(multipartRequest))
        {
            // Return via XHR
            ModelAndView view = new ModelAndView("jsonView");
            view.addObject(JsonView.JSON_DATA_KEY, data);
            return view;
        }
        else
        {
            // Return normally
            if (success)
            {
                actionUtils.flashInfo("Your object was saved.");
                Url u = new Url("/otherobjects/workbench/view/" + cmsFile.getEditableId());
                response.sendRedirect(u.toString());
                return null;
            }
            else
            {
                actionUtils.flashWarning("Your object could not be saved. See below for errors.");
                ModelAndView view = new ModelAndView("/otherobjects/templates/legacy/pages/edit");
                view.addObject("success", success);
                view.addObject("id", cmsFile.getEditableId());
                view.addObject("typeDef", typeDef);
                view.addObject("object", cmsFile);
                view.addObject("containerId", containerId);
                view.addObject("org.springframework.validation.BindingResult.object", errors);
                return view;
            }
        }

    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    public void setBindService(BindService bindService)
    {
        this.bindService = bindService;
    }

    public void setValidatorService(ValidatorService validatorService)
    {
        this.validatorService = validatorService;
    }

    public MimeType getMimeType(String filename)
    {
        try
        {
            MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
            return new MimeType(mimetypesFileTypeMap.getContentType(filename));
        }
        catch (Exception e)
        {
            return null;
        }
    }

}
