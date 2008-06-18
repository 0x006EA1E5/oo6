package org.otherobjects.cms.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.binding.BindService;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.GenericDao;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.model.CmsImageDao;
import org.otherobjects.cms.model.CompositeDatabaseId;
import org.otherobjects.cms.model.DbFolder;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeDefBuilder;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.IdentifierUtils;
import org.otherobjects.cms.util.ImageUtils;
import org.otherobjects.cms.validation.ValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.iptc.IptcDirectory;

/**
 * Controller to process form submission. Only data for types registered in the TypeService is supported.
 *
 * @author rich
 */
@Controller
public class FormController
{
    private final Logger logger = LoggerFactory.getLogger(FormController.class);

    @Resource
    private UniversalJcrDao universalJcrDao;

    @Resource
    private DaoService daoService;

    @Resource
    private TypeService typeService;

    // Used to build typeDefs for Hibernate persisted beans
    // FIXME Change to TypeService?
    @Resource
    private TypeDefBuilder typeDefBuilder;

    @Resource
    private BindService bindService;

    @Resource
    private ValidatorService validatorService;

    @SuppressWarnings("unchecked")
    @RequestMapping("/form/**")
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        // Spring convenience construct with access to locale settings and message source etc. as setup by the DispatcherServlet
        RequestContext requestContext = new RequestContext(request);

        Enumeration parameterNames = request.getParameterNames();
        logger.debug("Query String: ", request.getQueryString());
        while (parameterNames.hasMoreElements())
        {
            String name = (String) parameterNames.nextElement();
            logger.info("Parameter: {} = {}", name, request.getParameter(name));
        }

        try
        {
            BindingResult errors = null;
            // Get form info
            String id = request.getParameter("editableId");
            String typeName = request.getParameter("_oo_type");
            String containerId = request.getParameter("_oo_containerId");

            GenericDao genericDao = null;
            TypeDef typeDef = null;
            Object item = null;
            if (StringUtils.isNotEmpty(id))
            {
                if (IdentifierUtils.isUUID(id))
                {
                    genericDao = universalJcrDao;
                    item = genericDao.get(id);
                    typeDef = (TypeDef) PropertyUtils.getSimpleProperty(item, "typeDef"); //FIXME rather than object we should probably have some interface that has getTypeDef
                }
                else
                {
                    // TODO Should validate if plausable db id here and throw error if not
                    CompositeDatabaseId compositeDatabaseId = IdentifierUtils.getCompositeDatabaseId(id);
                    if (compositeDatabaseId != null)
                    {
                        genericDao = daoService.getDao(compositeDatabaseId.getClazz());
                        item = genericDao.get(compositeDatabaseId.getId());
                        typeDef = typeDefBuilder.getTypeDef(item.getClass());
                    }
                }
            }
            else if (StringUtils.isNotEmpty(typeName))
            {
                //TODO This needs to be extracted into an interface. Maybe DAO?
                BaseNode parent = universalJcrDao.get(containerId);
                if (parent instanceof DbFolder)
                {
                    item = Class.forName(typeName).newInstance();
                    typeDef = typeDefBuilder.getTypeDef(item.getClass());
                    genericDao = daoService.getDao(item.getClass());
                }
                else
                {
                    item = create(typeName);
                    ((BaseNode) item).setPath(parent.getJcrPath());
                    typeDef = ((BaseNode) item).getTypeDef();
                    genericDao = universalJcrDao;
                }
            }
            else
            {
                throw new IllegalArgumentException("Not enough information provided to save form data. Either existing id or new type required.");
            }

            Assert.notNull(item, "No object found for id: " + id);
            Assert.notNull(typeDef, "We cannot bind objects that don't have a typeDef");

            //deal with image file uploads
            if (request instanceof MultipartHttpServletRequest)
            {
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                //currently we only know how to deal with image uploads
                if (item instanceof CmsImage)
                {
                    return handleCmsImageRequest(multipartRequest, response, (CmsImage) item);
                }

            }

            // Perform validation
            if (item instanceof DynaNode)
            {
                // FIXME Need integrated DynaNode validation rather than this special case
                for (PropertyDef pd : typeDef.getProperties())
                {
                    if (pd.getType().equals("reference"))
                        ((DynaNode) item).set(pd.getName(), genericDao.get(request.getParameter(pd.getName())));
                    else if (pd.getType().equals("number"))
                        ((DynaNode) item).set(pd.getName(), Long.parseLong(request.getParameter(pd.getName())));
                    else
                        ((DynaNode) item).set(pd.getName(), request.getParameter(pd.getName()));

                }
            }
            else
            {
                errors = bindService.bind(item, typeDef, request);
                Validator validator = validatorService.getValidator(item);
                if (validator != null)
                    validator.validate(item, errors);
                else
                    logger.warn("No validator for item of class " + item.getClass() + " found");
            }

            if (!(errors != null && errors.hasErrors()))
            {
                // Save new object
                genericDao.save(item, false);
            }

            // We have errors so return error messages
            ModelAndView view = new ModelAndView("jsonView");
            if (errors != null && errors.getErrorCount() > 0)
            {
                List<Object> jsonErrors = new ArrayList<Object>();
                for (FieldError e : (List<FieldError>) errors.getFieldErrors())
                {
                    Map<String, String> error = new HashMap<String, String>();
                    error.put("id", e.getField());
                    error.put("msg", requestContext.getMessage(e.getCode(), e.getArguments()));
                    jsonErrors.add(error);
                }
                view.getModel().put("success", false);
                view.getModel().put("errors", jsonErrors);
                return view;
            }

            // All OK...
            view.getModel().put("success", true);
            view.getModel().put("formObject", item);
            return view;
        }
        catch (Exception e)
        {
            ModelAndView view = new ModelAndView("jsonView");
            view.getModel().put("success", false);
            view.getModel().put("message", e.getMessage());
            logger.error("Error saving form data.", e);
            return view;
        }
    }

    private ModelAndView handleCmsImageRequest(MultipartHttpServletRequest multipartRequest, HttpServletResponse response, CmsImage cmsImage) throws IOException
    {
        MultipartFile imageFile = (MultipartFile) multipartRequest.getFileMap().get("newFile");
        this.logger.info("Received file " + imageFile.getOriginalFilename());

        CmsImageDao cmsImageDao = (CmsImageDao) this.daoService.getDao(CmsImage.class);

        File newFile = File.createTempFile("upload", "img");
        newFile.deleteOnExit();

        imageFile.transferTo(newFile);

        cmsImage.setPath("/libraries/images/");
        cmsImage.setCode(imageFile.getOriginalFilename());
        cmsImage.setLabel(imageFile.getOriginalFilename());
        cmsImage.setNewFile(newFile);

        // some manual binding which is pants
        cmsImage.setDescription(multipartRequest.getParameter("description"));
        cmsImage.setKeywords(multipartRequest.getParameter("keywords"));
        cmsImage.setOriginalProvider(multipartRequest.getParameter("originalProvider"));
        cmsImage.setProviderId(multipartRequest.getParameter("providerId"));
        cmsImage.setCopyright(multipartRequest.getParameter("copyright"));

        // Look for IPTC tags to read
        Metadata imageMetadata = ImageUtils.getImageMetadata(newFile);
        if (imageMetadata.containsDirectory(IptcDirectory.class))
        {
            Directory iptc = imageMetadata.getDirectory(IptcDirectory.class);
            if (iptc.containsTag(IptcDirectory.TAG_OBJECT_NAME))
                cmsImage.setLabel(iptc.getString(IptcDirectory.TAG_OBJECT_NAME));
            if (iptc.containsTag(IptcDirectory.TAG_CAPTION))
                cmsImage.setDescription(iptc.getString(IptcDirectory.TAG_CAPTION));
            if (iptc.containsTag(IptcDirectory.TAG_COPYRIGHT_NOTICE))
                cmsImage.setCopyright(iptc.getString(IptcDirectory.TAG_COPYRIGHT_NOTICE));
            if (iptc.containsTag(IptcDirectory.TAG_KEYWORDS))
                cmsImage.setKeywords(iptc.getString(IptcDirectory.TAG_KEYWORDS));
        }

        // Get file proprerties
        cmsImageDao.save(cmsImage);
        newFile.delete();

        // We have errors so return error messages
        ModelAndView view = new ModelAndView("jsonView");
        view.addObject("mimeOverride", "text/html");

        // All OK...
        view.getModel().put("success", true);
        view.getModel().put("formObject", cmsImage);
        return view;
    }

    private BaseNode create(String typeName)
    {
        try
        {
            Object newInstance = Class.forName(typeName).newInstance();
            if (newInstance instanceof BaseNode)
            {
                ((BaseNode) newInstance).setOoType(typeName);
            }
            return (BaseNode) newInstance;
        }
        catch (Exception e)
        {
            // TODO This should detect DynaNodes before exception...

            // Couldn't create real class so use DynaNode instead 
            if (typeService.getType(typeName) != null)
                return new DynaNode(typeName);
            else
                throw new OtherObjectsException("Could not create object of type: " + typeName, e);
        }
    }

}
