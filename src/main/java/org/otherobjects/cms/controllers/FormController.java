package org.otherobjects.cms.controllers;import java.util.ArrayList;import java.util.Enumeration;import java.util.HashMap;import java.util.List;import java.util.Map;import javax.annotation.Resource;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import org.apache.commons.beanutils.PropertyUtils;import org.apache.commons.lang.StringUtils;import org.otherobjects.cms.OtherObjectsException;import org.otherobjects.cms.binding.BindService;import org.otherobjects.cms.dao.DaoService;import org.otherobjects.cms.dao.GenericDao;import org.otherobjects.cms.jcr.UniversalJcrDao;import org.otherobjects.cms.model.BaseNode;import org.otherobjects.cms.model.CompositeDatabaseId;import org.otherobjects.cms.types.TypeDef;import org.otherobjects.cms.types.TypeDefBuilder;import org.otherobjects.cms.util.IdentifierUtils;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.stereotype.Controller;import org.springframework.util.Assert;import org.springframework.validation.BindingResult;import org.springframework.validation.FieldError;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.servlet.ModelAndView;import org.springframework.web.servlet.support.RequestContext;/** * Controller to process form submission. Only data for types registered in the TypeService is supported. * * @author rich */@Controllerpublic class FormController{    private final Logger logger = LoggerFactory.getLogger(FormController.class);    @Resource    private UniversalJcrDao universalJcrDao;        @Resource    private DaoService daoService;        // Used to build typeDefs for Hibernate persisted beans     @Resource    private TypeDefBuilder typeDefBuilder;    @Resource    private BindService bindService;    @SuppressWarnings("unchecked")    @RequestMapping("/form/**")    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception    {        // Spring convenience construct with access to locale settings and message source etc. as setup by the DispatcherServlet        RequestContext requestContext = new RequestContext(request);        Enumeration parameterNames = request.getParameterNames();        logger.debug("Query String: ", request.getQueryString());        while (parameterNames.hasMoreElements())        {            String name = (String) parameterNames.nextElement();            logger.info("Parameter: {} = {}", name, request.getParameter(name));        }        try        {            BindingResult errors = null;            // Get form info            String id = request.getParameter("editableId");            String typeName = request.getParameter("_oo_type");            String containerId = request.getParameter("_oo_containerId");            GenericDao genericDao = null;            TypeDef typeDef = null;            Object item = null;            if (StringUtils.isNotEmpty(id))            {                if (IdentifierUtils.isUUID(id))                {                    genericDao = universalJcrDao;                    item = genericDao.get(id);                    typeDef = (TypeDef) PropertyUtils.getSimpleProperty(item, "typeDef"); //FIXME rather than object we should probably have some interface that has getTypeDef                }                else                {                    CompositeDatabaseId compositeDatabaseId = IdentifierUtils.getCompositeDatabaseId(id);                    if (compositeDatabaseId != null)                    {                        genericDao = daoService.getDao(compositeDatabaseId.getClazz());                        item = genericDao.get(compositeDatabaseId.getId());                        typeDef = typeDefBuilder.getTypeDef(item.getClass());                    }                }            }            else if (StringUtils.isNotEmpty(typeName))            {                BaseNode parent = universalJcrDao.get(containerId);                item = create(typeName);                ((BaseNode) item).setPath(parent.getJcrPath());                typeDef = ((BaseNode) item).getTypeDef();                genericDao = universalJcrDao;            }            else            {                throw new IllegalArgumentException("Not enough information provided to save form data. Either existing id or new type required.");            }            Assert.notNull(typeDef, "We cannot bind objects that don't have a typeDef");            // Perform validation            errors = bindService.bind(item, typeDef, request);            //if(item instanceof BaseNode) //FIXME we need a way to specify validators for non DynaNodes            //	validator.validate(item, errors);            if (!errors.hasErrors())            {                // Save new object                genericDao.save(item, false);            }            // We have errors so return error messages            ModelAndView view = new ModelAndView("jsonView");            if (errors != null && errors.getErrorCount() > 0)            {                List<Object> jsonErrors = new ArrayList<Object>();                for (FieldError e : (List<FieldError>) errors.getFieldErrors())                {                    Map<String, String> error = new HashMap<String, String>();                    error.put("id", e.getField());                    error.put("msg", requestContext.getMessage(e.getCode(), e.getArguments()));                    jsonErrors.add(error);                }                view.getModel().put("success", false);                view.getModel().put("errors", jsonErrors);                return view;            }            // All OK...            view.getModel().put("success", true);            view.getModel().put("formObject", item);            return view;        }        catch (Exception e)        {            ModelAndView view = new ModelAndView("jsonView");            view.getModel().put("success", false);            view.getModel().put("message", e.getMessage());            logger.error("Error saving form data.", e);            return view;        }    }    private BaseNode create(String typeName)    {        try        {            // Should this use DynaNode            Object newInstance = Class.forName(typeName).newInstance();            if (newInstance instanceof BaseNode)            {                ((BaseNode) newInstance).setOoType(typeName);            }            return (BaseNode) newInstance;        }        catch (Exception e)        {            throw new OtherObjectsException("Could not create object of type: " + typeName, e);        }    }}