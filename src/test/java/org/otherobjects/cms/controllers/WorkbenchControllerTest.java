package org.otherobjects.cms.controllers;

import junit.framework.TestCase;

import org.otherobjects.cms.binding.BindServiceImpl;
import org.otherobjects.cms.binding.MockWebApplicationContext;
import org.otherobjects.framework.SingletonBeanLocator;
import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.otherobjects.cms.dao.DaoServiceImpl;
import org.otherobjects.cms.dao.MockGenericDao;
import org.otherobjects.cms.datastore.HibernateDataStore;
import org.otherobjects.cms.model.Role;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefImpl;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.TypeServiceImpl;
import org.otherobjects.cms.validation.TypeDefConfiguredValidator;
import org.otherobjects.cms.validation.ValidatorServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;

public class WorkbenchControllerTest extends TestCase
{
    private TypeService typeService = new TypeServiceImpl();
    private DaoServiceImpl daoService = new DaoServiceImpl();
    
    @Override
    protected void setUp() throws Exception
    {
        //setup type service for Template, TemplateRegion TemplateBlock
        super.setUp();
        AnnotationBasedTypeDefBuilder typeDefBuilder = new AnnotationBasedTypeDefBuilder();
        OtherObjectsConfigurator otherObjectsConfigurator = new OtherObjectsConfigurator();
        otherObjectsConfigurator.setProperty("otherobjects.default.date.format", "yyy-MM-dd");
        otherObjectsConfigurator.setProperty("otherobjects.default.time.format", "yyy-MM-dd");
        otherObjectsConfigurator.setProperty("otherobjects.default.timestamp.format", "yyy-MM-dd");
        typeDefBuilder.setOtherObjectsConfigurator(otherObjectsConfigurator);
        typeDefBuilder.afterPropertiesSet();
        SingletonBeanLocator.registerTestBean("typeService", typeService);
        SingletonBeanLocator.registerTestBean("otherObjectsConfigurator", otherObjectsConfigurator);

        typeService.registerType(typeDefBuilder.getTypeDef(Role.class));
        ((TypeServiceImpl) typeService).reset();
        
        // Add DynaNode type
        TypeDefImpl td = new TypeDefImpl("ArticlePage");
        td.setLabelProperty("title");
        td.addProperty(new PropertyDefImpl("title", "string", null, null, true));
        typeService.registerType(td);
        
        // Register mock DAOs
        MockGenericDao roleDao = new MockGenericDao();
        daoService.addDao(Role.class.getName(), roleDao);
    }
    
    public void testHandleRequest() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();

        ApplicationContext ac = new MockWebApplicationContext();
        request.setAttribute(RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);

        WorkbenchController controller = new WorkbenchController();
        controller.setTypeService(typeService);
        controller.setHibernateDataStore(new HibernateDataStore(daoService));
        controller.setDaoService(daoService);
        controller.setBindService(new BindServiceImpl());
        
        ValidatorServiceImpl validatorService = new ValidatorServiceImpl();
        TypeDefConfiguredValidator validator = new TypeDefConfiguredValidator();
        validator.setTypeService(typeService);
        validatorService.setDefaultValidator(validator);
        controller.setValidatorService(validatorService);

        
        // Hibernate stored type
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addParameter("_oo_type", Role.class.getName());
        request.addParameter("name","My Role");
        request.addParameter("description","My description");
        ModelAndView mav = controller.save(request, response);
        assertNull(mav);
    }
}
