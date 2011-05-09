/*
 * This file is part of the OTHERobjects Content Management System.
 * 
 * Copyright 2007-2009 OTHER works Limited.
 * 
 * OTHERobjects is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OTHERobjects is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OTHERobjects.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.otherobjects.cms.controllers;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.Url;
import org.otherobjects.cms.binding.BindService;
import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.datastore.JackrabbitDataStore;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.tools.CmsImageTool;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.ActionUtils;
import org.otherobjects.cms.util.ImageUtils;
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

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.iptc.IptcDirectory;

/**
 * Controller to process form submissions. Only data for types registered in the TypeService is supported.
 *
 * @author rich
 */
@Controller
@SuppressWarnings("unchecked")
public class CmsImageController
{
    private final Logger logger = LoggerFactory.getLogger(CmsImageController.class);

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

    @Resource
    private CmsImageTool cmsImageTool;

    @RequestMapping({"/image/save"})
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

        CmsImage cmsImage = null;
        GenericJcrDao genericDao = (GenericJcrDao) jackrabbitDataStore.getDao(typeDef);
        boolean isNew = true;
        if (StringUtils.isNotBlank(id))
        {
            isNew = false;
            cmsImage = (CmsImage) jackrabbitDataStore.get(id);
        }
        else
        {
            cmsImage = (CmsImage) jackrabbitDataStore.create(typeDef, containerId);
        }

        Assert.notNull(cmsImage, "Could not prepare item for binding: " + typeName);

        // Bind and validate
        BindingResult errors = null;
        errors = bindService.bind(cmsImage, typeDef, multipartRequest);
        Validator validator = validatorService.getValidator(cmsImage);
        if (validator != null)
            validator.validate(cmsImage, errors);
        else
            logger.warn("No validator for item of class " + cmsImage.getClass() + " found");

        // Set fileName so that code can be generated
        if (StringUtils.isBlank(id))
        {
            // FIXME This should be done in the validation
            Assert.notNull(cmsImage.getNewFile(), "A file must be uploaded when creating new images.");
            cmsImage.setOriginalFileName(cmsImage.getNewFile().getOriginalFilename());

            // Check if we have a image with the same name already stored
            int suffix = 2;
            Object existingImage = genericDao.getByPath(cmsImage.getJcrPath());
            String fileStem = StringUtils.substringBeforeLast(cmsImage.getCode(), ".");
            while (existingImage != null)
            {
                cmsImage.setCode(fileStem + "-" + (suffix++) + "." + cmsImage.getExtension());
                existingImage = genericDao.getByPath(cmsImage.getJcrPath());
            }
        }
        if (cmsImage.getNewFile() != null)
        {
            // Save image information
            Dimension imageDimensions = ImageUtils.getImageDimensions(cmsImage.getNewFile().getInputStream());
            cmsImage.setOriginalWidth(new Double(imageDimensions.getWidth()).longValue());
            cmsImage.setOriginalHeight(new Double(imageDimensions.getHeight()).longValue());

            // Look for IPTC tags to read
            if (cmsImage.getExtension().endsWith("jpg")) // TODO Use MimeType instead
            {
                Metadata imageMetadata = ImageUtils.getImageMetadata(cmsImage.getNewFile().getInputStream());
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
            }

            // Make sure we always have a label
            if (cmsImage.getLabel() == null)
                cmsImage.setLabel(cmsImage.getCode());

        }

        // Save
        boolean success = false;
        if (!(errors != null && errors.hasErrors()))
        {
            // Save new object
            cmsImage = (CmsImage) genericDao.save(cmsImage, false);
            success = true;
        }

        if (cmsImage.getNewFile() != null)
        {
            // Store image in originals folder
            if (!isNew) // clean up old image resource - we are replacing an existing image
            {
                cleanupExisting(cmsImage);
            }
            OoResource resource = ooResourceLoader.getResource(CmsImage.DEFAULT_FOLDER + CmsImage.ORIGINALS_PATH + cmsImage.getCode());
            cmsImage.getNewFile().transferTo(resource.getFile());

            // Create thumbnail
            this.cmsImageTool.getThumbnail(cmsImage);
        }

        // Prepare return data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("success", success);
        data.put("formObject", cmsImage);
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
                Url u = new Url("/otherobjects/workbench/view/" + cmsImage.getEditableId());
                response.sendRedirect(u.toString());
                return null;
            }
            else
            {
                actionUtils.flashWarning("Your object could not be saved. See below for errors.");
                ModelAndView view = new ModelAndView("/otherobjects/templates/legacy/pages/edit");
                view.addObject("success", success);
                view.addObject("id", cmsImage.getEditableId());
                view.addObject("typeDef", typeDef);
                view.addObject("object", cmsImage);
                view.addObject("containerId", containerId);
                view.addObject("org.springframework.validation.BindingResult.object", errors);
                return view;
            }
        }

    }

    /**
     * Imports an image from a POST. Used for Picnik integration.
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping({"/image/import"})
    public ModelAndView importImage(HttpServletRequest request, HttpServletResponse response) throws IOException
    {

        RequestUtils.logParameters(logger, request);


        // Prepare object (create or fetch)
        String id = request.getParameter("id");
        String imageUrl = request.getParameter("img");

        InputStream in = new URL(imageUrl).openStream();
        byte[] imageData = null;
        try
        {
            imageData = IOUtils.toByteArray(in);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }

        String typeName = CmsImage.class.getName();
        Assert.notNull(typeName, "Type name not specified in _oo_type parameter.");
        TypeDef typeDef = typeService.getType(typeName);
        Assert.notNull(typeDef, "No typeDef found for type: " + typeName);

        CmsImage cmsImage = null;
        GenericJcrDao genericDao = (GenericJcrDao) jackrabbitDataStore.getDao(typeDef);
        cmsImage = (CmsImage) jackrabbitDataStore.get(id);
        cmsImage.setId(null);

        // Check if we have a image with the same name already stored
        int suffix = 2;
        Object existingImage = genericDao.getByPath(cmsImage.getJcrPath());
        String fileStem = StringUtils.substringBeforeLast(cmsImage.getCode(), ".");
        while (existingImage != null)
        {
            cmsImage.setCode(fileStem + "-" + (suffix++) + "." + cmsImage.getExtension());
            existingImage = genericDao.getByPath(cmsImage.getJcrPath());
        }

        // Save image information
        Dimension imageDimensions = ImageUtils.getImageDimensions(new ByteArrayInputStream(imageData));
        cmsImage.setOriginalWidth(new Double(imageDimensions.getWidth()).longValue());
        cmsImage.setOriginalHeight(new Double(imageDimensions.getHeight()).longValue());

        // Make sure we always have a label
        if (cmsImage.getLabel() == null)
            cmsImage.setLabel(cmsImage.getCode());

        // Save
        cmsImage = (CmsImage) genericDao.save(cmsImage, false);

        OoResource resource = ooResourceLoader.getResource(CmsImage.DEFAULT_FOLDER + CmsImage.ORIGINALS_PATH + cmsImage.getCode());
        FileUtils.writeByteArrayToFile(resource.getFile(), imageData);

        // Create thumbnail
        this.cmsImageTool.getThumbnail(cmsImage);

        // Prepare return data
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("success", true);
        data.put("formObject", cmsImage);

        ActionUtils actionUtils = new ActionUtils(request, response, null, null);
        actionUtils.flashInfo("Your object was saved.");
        Url u = new Url("/otherobjects/workbench/view/" + cmsImage.getEditableId());
        response.sendRedirect(u.toString());
        return null;

    }

    private void cleanupExisting(CmsImage cmsImage)
    {
        try
        {
            OoResource imageDir = ooResourceLoader.getResource(CmsImage.DEFAULT_FOLDER);
            for (File oldImage : (Collection<File>) FileUtils.listFiles(imageDir.getFile(), new NameFileFilter(cmsImage.getCode()), TrueFileFilter.INSTANCE))
            {
                oldImage.delete();
            }
        }
        catch (IOException e)
        {
            logger.warn("Couldn't cleanup preexisting image files", e);
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
}
