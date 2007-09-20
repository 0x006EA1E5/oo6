package org.otherobjects.cms.controllers;import java.util.ArrayList;import java.util.Enumeration;import java.util.HashMap;import java.util.List;import java.util.Map;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import org.apache.commons.beanutils.PropertyUtils;import org.otherobjects.cms.binding.BindService;import org.otherobjects.cms.dao.DaoService;import org.otherobjects.cms.dao.DynaNodeDao;import org.otherobjects.cms.dao.GenericDao;import org.otherobjects.cms.model.CompositeDatabaseId;import org.otherobjects.cms.model.DynaNode;import org.otherobjects.cms.types.TypeDef;import org.otherobjects.cms.types.TypeDefBuilder;import org.otherobjects.cms.util.IdentifierUtils;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.util.Assert;import org.springframework.validation.BindingResult;import org.springframework.validation.FieldError;import org.springframework.validation.Validator;import org.springframework.web.servlet.ModelAndView;import org.springframework.web.servlet.mvc.Controller;import org.springframework.web.servlet.support.RequestContext;/** * Controller to process form submission. Only data for types registered in the TypeService is supported. * * @author rich */public class FormController implements Controller{    private final Logger logger = LoggerFactory.getLogger(FormController.class);    private DynaNodeDao dynaNodeDao;    private DaoService daoService;    private TypeDefBuilder typeDefBuilder;    private BindService bindService;    private Validator validator;    @SuppressWarnings("unchecked")    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception    {        // Spring convenience construct with access to locale settings and message source etc. as setup by the DispatcherServlet        RequestContext requestContext = new RequestContext(request);        Enumeration parameterNames = request.getParameterNames();        logger.error("Query String: ", request.getQueryString());        while (parameterNames.hasMoreElements())        {            String name = (String) parameterNames.nextElement();            logger.error("Parameter: {} = {}", name, request.getParameter(name));        }        try        {            BindingResult errors = null;            // Get form info            String id = request.getParameter("editableId");            Assert.hasLength(id, "An existing id is required.");            //TODO M2 Type only needed if adding new objects            //String typeName = request.getParameter("ooType");            //Assert.notNull(typeName, "Type must be provided in form data.");            // Load existing object and create a proper bean from the type info            //DynaNode dynaNode = dynaNodeDao.get(id);                                    GenericDao genericDao = null;            TypeDef typeDef = null;            Object item = null;            if(IdentifierUtils.isUUID(id))            {    	        genericDao = dynaNodeDao;            	item = genericDao.get(id);            	typeDef = (TypeDef) PropertyUtils.getSimpleProperty(item, "typeDef"); //FIXME rather than object we should probably have some interface that has getTypeDef    	    }            else            {            	CompositeDatabaseId compositeDatabaseId = IdentifierUtils.getCompositeDatabaseId(id);            	if(compositeDatabaseId != null)            	{            		genericDao = daoService.getDao(compositeDatabaseId.getClazz());            		item = genericDao.get(compositeDatabaseId.getId());            		typeDef = typeDefBuilder.getTypeDef(item.getClass());            	}            }                        Assert.notNull(typeDef, "We cannot bind objects that don't have a typeDef");                        // Perform validation            errors = bindService.bind(item, typeDef, request);            if(item instanceof DynaNode) //FIXME we need a way to specify validators for non DynaNodes            	validator.validate(item, errors);            if (!errors.hasErrors())            {                // Save new object            	genericDao.save(item, false);            }            // We have errors so return error messages            ModelAndView view = new ModelAndView("jsonView");            if (errors != null && errors.getErrorCount() > 0)            {                List<Object> jsonErrors = new ArrayList<Object>();                for (FieldError e : (List<FieldError>) errors.getFieldErrors())                {                    Map<String, String> error = new HashMap<String, String>();                    error.put("id", e.getField());                    error.put("msg", requestContext.getMessage(e.getCode(), e.getArguments()));                    jsonErrors.add(error);                }                view.getModel().put("success", false);                view.getModel().put("errors", jsonErrors);                return view;            }            // All OK...            view.getModel().put("success", true);            view.getModel().put("formObject", item);            return view;        }        catch (Exception e)        {            ModelAndView view = new ModelAndView("jsonView");            view.getModel().put("success", false);            view.getModel().put("message", e.getMessage());            logger.error("Error saving form data.", e);            return view;        }    }    public DynaNodeDao getDynaNodeDao()    {        return dynaNodeDao;    }    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)    {        this.dynaNodeDao = dynaNodeDao;    }    public void setValidator(Validator validator)    {        this.validator = validator;    }    public void setBindService(BindService bindService)    {        this.bindService = bindService;    }	public void setDaoService(DaoService daoService) {		this.daoService = daoService;	}	public void setTypeDefBuilder(TypeDefBuilder typeDefBuilder) {		this.typeDefBuilder = typeDefBuilder;	}    	    }