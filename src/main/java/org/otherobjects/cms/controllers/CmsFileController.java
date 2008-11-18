//package org.otherobjects.cms.controllers;
//
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang.StringUtils;
//import org.otherobjects.cms.OtherObjectsException;
//import org.otherobjects.cms.Url;
//import org.otherobjects.cms.binding.BindService;
//import org.otherobjects.cms.dao.DaoService;
//import org.otherobjects.cms.dao.GenericDao;
//import org.otherobjects.cms.jcr.dynamic.DynaNode;
//import org.otherobjects.cms.model.BaseNode;
//import org.otherobjects.cms.model.CompositeDatabaseId;
//import org.otherobjects.cms.model.Editable;
//import org.otherobjects.cms.types.TypeDef;
//import org.otherobjects.cms.types.TypeService;
//import org.otherobjects.cms.util.ActionUtils;
//import org.otherobjects.cms.util.IdentifierUtils;
//import org.otherobjects.cms.util.RequestUtils;
//import org.otherobjects.cms.validation.ValidatorService;
//import org.otherobjects.cms.views.JsonView;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.util.Assert;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.validation.Validator;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.ModelAndView;
//
///**
// * Controller to process form submissions. Only data for types registered in the TypeService is supported.
// *
// * @author rich
// */
//@Controller
//@SuppressWarnings("unchecked")
//public class CmsFileController
//{
//    private final Logger logger = LoggerFactory.getLogger(FormController.class);
//
//    @Resource
//    private DaoService daoService;
//
//    @Resource
//    private TypeService typeService;
//
//    @Resource
//    private BindService bindService;
//
//    @Resource
//    private ValidatorService validatorService;
//
////    @Resource
////    private OoResourceLoader ooResourceLoader;
//
//    @RequestMapping("/form/**")
//    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
//    {
//        //        //deal with image file uploads
//        //        if (request instanceof MultipartHttpServletRequest)
//        //        {
//        //            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//        //            //currently we only know how to deal with image uploads
//        //            if (item instanceof CmsImage)
//        //            {
//        //                return handleCmsImageRequest(multipartRequest, response, (CmsImage) item);
//        //            }
//        //            else if (item instanceof CmsFile)
//        //            {
//        //                return handleCmsFileRequest(multipartRequest, response, (CmsFile) item);
//        //            }
//        //        }
//
//        ActionUtils actionUtils = new ActionUtils(request, response, null, null);
////        RequestContext requestContext = new RequestContext(request);
//
//        // Debug parameters
//        Enumeration parameterNames = request.getParameterNames();
//        logger.debug("Query String: ", request.getQueryString());
//        while (parameterNames.hasMoreElements())
//        {
//            String name = (String) parameterNames.nextElement();
//            logger.info("Parameter: {} = {}", name, request.getParameter(name));
//        }
//
//        // Prepare object (create or fetch)
//        String id = request.getParameter("_oo_id");
//        String typeName = request.getParameter("_oo_type");
//        String containerId = request.getParameter("_oo_containerId");
//        Assert.notNull(typeName, "Type name not specified in _oo_type parameter.");
//        TypeDef type = typeService.getType(typeName);
//        Assert.notNull(type, "No typeDef found for type: " + typeName);
//
//        Editable item = null;
//        GenericDao genericDao = null;
//        if (type.getStore().equals(TypeDef.JACKRABBIT))
//        {
//            genericDao = daoService.getDao(typeName);
//            item = prepareJackrabbit(genericDao, type, containerId, id);
//        }
//        else
//        {
//            genericDao = daoService.getDao(typeName);
//            item = prepareHibernate(genericDao, type, containerId, id);
//        }
//        Assert.notNull(item, "Could not prepare item for binding: " + typeName);
//
//        // Bind and validate
//        BindingResult errors = null;
//        errors = bindService.bind(item, type, request);
//        Validator validator = validatorService.getValidator(item);
//        if (validator != null)
//            validator.validate(item, errors);
//        else
//            logger.warn("No validator for item of class " + item.getClass() + " found");
//
//        // Save
//        boolean success = false;
//        if (!(errors != null && errors.hasErrors()))
//        {
//            // Save new object
//            item = (Editable) genericDao.save(item, false);
//            success = true;
//        }
//
//        // Prepare return data
//        Map<String, Object> data = new HashMap<String, Object>();
//        data.put("success", success);
//        data.put("formObject", item);
//        if (!success)
//        {
//            List<Object> jsonErrors = new ArrayList<Object>();
//            for (FieldError e : (List<FieldError>) errors.getFieldErrors())
//            {
//                Map<String, String> error = new HashMap<String, String>();
//                error.put("id", e.getField());
//                //error.put("msg", requestContext.getMessage(e.getCode(), e.getArguments()));
//                jsonErrors.add(error);
//            }
//            data.put("success", false);
//            data.put("errors", jsonErrors);
//        }
//
//        // Return
//        if (RequestUtils.isXhr(request))
//        {
//            // Return via XHR
//            ModelAndView view = new ModelAndView("jsonView");
//            view.addObject(JsonView.JSON_DATA_KEY, data);
//            return view;
//        }
//        else
//        {
//            // Return normally
//            if (success)
//            {
//                actionUtils.flashInfo("Your object was saved.");
//                Url u = new Url("/otherobjects/workbench/view/" + item.getEditableId());
//                response.sendRedirect(u.toString());
//                return null;
//            }
//            else
//            {
//                actionUtils.flashWarning("Your object could not be saved. See below for errors.");
//                ModelAndView view = new ModelAndView("/otherobjects/templates/legacy/pages/edit");
//                view.addObject("success", success);
//                view.addObject("id", item.getEditableId());
//                view.addObject("typeDef", type);
//                view.addObject("object", item);
//                view.addObject("containerId", containerId);
//                view.addObject("org.springframework.validation.BindingResult.object", errors);
//                return view;
//            }
//        }
//    }
//
//    private Editable prepareHibernate(GenericDao genericDao, TypeDef type, String containerId, String id)
//    {
//        if (StringUtils.isNotEmpty(id))
//        {
//            CompositeDatabaseId compositeDatabaseId = IdentifierUtils.getCompositeDatabaseId(id);
//            if (compositeDatabaseId != null)
//            {
//                genericDao = daoService.getDao(compositeDatabaseId.getClazz());
//                return (Editable) genericDao.get(compositeDatabaseId.getId());
//            }
//        }
//        else
//        {
//            try
//            {
//                return (Editable) Class.forName(type.getClassName()).newInstance();
//            }
//            catch (Exception e)
//            {
//                throw new OtherObjectsException("Could not create object of type: " + type.getName(), e);
//            }
//        }
//        throw new OtherObjectsException("Could not create object of type: " + type.getName());
//    }
//
//    private Editable prepareJackrabbit(GenericDao genericDao, TypeDef type, String containerId, String id)
//    {
//        if (StringUtils.isNotEmpty(id))
//        {
//            return (Editable) genericDao.get(id);
//        }
//        else
//        {
//            return create(type.getName());
//        }
//    }
//
////    private ModelAndView handleCmsFileRequest(MultipartHttpServletRequest multipartRequest, HttpServletResponse response, CmsFile cmsFile) throws IOException
////    {
////        MultipartFile uploadFile = (MultipartFile) multipartRequest.getFileMap().get("file");
////        this.logger.info("Received file " + uploadFile.getOriginalFilename());
////
////        CmsFileDao cmsFileDao = (CmsFileDao) this.daoService.getDao(CmsFile.class);
////
////        OoResource uploadResource = ooResourceLoader.getResource(OoResourcePathPrefix.UPLOAD.pathPrefix() + uploadFile.getOriginalFilename()); //FIXME needs to be unique ensured
////
////        uploadFile.transferTo(uploadResource.getFile());
////        uploadResource.getFile().deleteOnExit();
////
////        cmsFile.setFile(uploadResource);
////        cmsFile.setOriginalFileName(uploadFile.getOriginalFilename());
////        cmsFile.setLabel(uploadFile.getOriginalFilename());
////
////        // some manual binding which is pants
////        cmsFile.setDescription(multipartRequest.getParameter("description"));
////        cmsFile.setKeywords(multipartRequest.getParameter("keywords"));
////        cmsFile.setCopyright(multipartRequest.getParameter("copyright"));
////
////        cmsFileDao.save(cmsFile);
////
////        System.out.println("##### " + cmsFile.getFile().getUrl().toString());
////
////        // We have errors so return error messages
////        ModelAndView view = new ModelAndView("jsonView");
////        view.addObject("mimeOverride", "text/html");
////
////        // All OK...
////        view.addObject("success", true);
////        view.addObject("formObject", cmsFile);
////        return view;
////    }
////
////    private ModelAndView handleCmsImageRequest(MultipartHttpServletRequest multipartRequest, HttpServletResponse response, CmsImage cmsImage) throws IOException
////    {
////        MultipartFile imageFile = (MultipartFile) multipartRequest.getFileMap().get("newFile");
////        this.logger.info("Received file " + imageFile.getOriginalFilename());
////
////        CmsImageDao cmsImageDao = (CmsImageDao) this.daoService.getDao(CmsImage.class);
////
////        File newFile = File.createTempFile("upload", "img");
////        newFile.deleteOnExit();
////
////        imageFile.transferTo(newFile);
////
////        cmsImage.setPath("/libraries/images/");
////        cmsImage.setCode(imageFile.getOriginalFilename());
////        cmsImage.setLabel(imageFile.getOriginalFilename());
////        cmsImage.setNewFile(newFile);
////
////        // some manual binding which is pants
////        cmsImage.setDescription(multipartRequest.getParameter("description"));
////        cmsImage.setKeywords(multipartRequest.getParameter("keywords"));
////        cmsImage.setOriginalProvider(multipartRequest.getParameter("originalProvider"));
////        cmsImage.setProviderId(multipartRequest.getParameter("providerId"));
////        cmsImage.setCopyright(multipartRequest.getParameter("copyright"));
////
////        // Look for IPTC tags to read
////        Metadata imageMetadata = ImageUtils.getImageMetadata(newFile);
////        if (imageMetadata.containsDirectory(IptcDirectory.class))
////        {
////            Directory iptc = imageMetadata.getDirectory(IptcDirectory.class);
////            if (iptc.containsTag(IptcDirectory.TAG_OBJECT_NAME))
////                cmsImage.setLabel(iptc.getString(IptcDirectory.TAG_OBJECT_NAME));
////            if (iptc.containsTag(IptcDirectory.TAG_CAPTION))
////                cmsImage.setDescription(iptc.getString(IptcDirectory.TAG_CAPTION));
////            if (iptc.containsTag(IptcDirectory.TAG_COPYRIGHT_NOTICE))
////                cmsImage.setCopyright(iptc.getString(IptcDirectory.TAG_COPYRIGHT_NOTICE));
////            if (iptc.containsTag(IptcDirectory.TAG_KEYWORDS))
////                cmsImage.setKeywords(iptc.getString(IptcDirectory.TAG_KEYWORDS));
////        }
////
////        // Get file proprerties
////        cmsImageDao.save(cmsImage);
////        newFile.delete();
////
////        // We have errors so return error messages
////        ModelAndView view = new ModelAndView("jsonView");
////        view.addObject("mimeOverride", "text/html");
////
////        // All OK...
////        view.addObject("success", true);
////        view.addObject("formObject", cmsImage);
////        return view;
////    }
//
//    private BaseNode create(String typeName)
//    {
//        try
//        {
//            Object newInstance = Class.forName(typeName).newInstance();
//            if (newInstance instanceof BaseNode)
//            {
//                ((BaseNode) newInstance).setOoType(typeName);
//            }
//            return (BaseNode) newInstance;
//        }
//        catch (Exception e)
//        {
//            // TODO This should detect DynaNodes before exception...
//
//            // Couldn't create real class so use DynaNode instead 
//            if (typeService.getType(typeName) != null)
//                return new DynaNode(typeName);
//            else
//                throw new OtherObjectsException("Could not create object of type: " + typeName, e);
//        }
//    }
//
//    public void setDaoService(DaoService daoService)
//    {
//        this.daoService = daoService;
//    }
//
//    public void setTypeService(TypeService typeService)
//    {
//        this.typeService = typeService;
//    }
//
//    public void setBindService(BindService bindService)
//    {
//        this.bindService = bindService;
//    }
//
//    public void setValidatorService(ValidatorService validatorService)
//    {
//        this.validatorService = validatorService;
//    }
//}
