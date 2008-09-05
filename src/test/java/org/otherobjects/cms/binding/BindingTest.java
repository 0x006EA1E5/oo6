package org.otherobjects.cms.binding;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefBuilder;
import org.otherobjects.cms.types.TypeDefImpl;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.TypeServiceImpl;
import org.otherobjects.cms.util.ActionUtils;
import org.otherobjects.cms.validation.TypeDefConfiguredValidator;
import org.otherobjects.cms.validation.ValidatorServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;

/**
 * Test binding of objects to forms.
 * 
 * 3 key types: new object, existing object, binding result (form previous submission)
 * 
 * @author rich
 */
public class BindingTest extends TestCase
{
    private TypeService typeService = new TypeServiceImpl();
    private ValidatorServiceImpl validatorService;
    
    @Override
    protected void setUp() throws Exception
    {
        // Setup type service
        super.setUp();
        SingletonBeanLocator.registerTestBean("typeService", typeService);
        TypeDefBuilder typeDefBuilder = new AnnotationBasedTypeDefBuilder();
        typeService.registerType(typeDefBuilder.getTypeDef(TestObject.class));
        ((TypeServiceImpl) typeService).reset();

        // Add DynaNode type
        TypeDefImpl td = new TypeDefImpl("ArticlePage");
        td.setLabelProperty("title");
        td.addProperty(new PropertyDefImpl("title", "string", null, null, true, true));
        typeService.registerType(td);
        
        // Setup validator
        validatorService = new ValidatorServiceImpl();
        TypeDefConfiguredValidator validator = new TypeDefConfiguredValidator();
        validator.setTypeService(typeService);
        validatorService.setDefaultValidator(validator);
    }

    // Test new form
    public void testBindNew()
    {
        ApplicationContext ac = new MockWebApplicationContext();
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);

        // Bean
        TestObject t = new TestObject();
        ModelMap model = new ModelMap();
        model.addAttribute("testObject", t);
        RequestContext rc = new RequestContext(request, model);
        BindStatus bindStatus = rc.getBindStatus("testObject.testString");
        assertEquals(null, bindStatus.getValue());

        //        // DynaNode
        //        DynaNode article = new DynaNode("ArticlePage");
        //        model.addAttribute("article", article);
        //        rc = new OORequestContext(request, model);
        //        bindStatus = rc.getBindStatus("article.title");
        //        assertEquals(null, bindStatus.getValue());
        //        
    }

    // Test bind existing object
    public void testBindExisting()
    {
        ApplicationContext ac = new MockWebApplicationContext();
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);

        TestObject obj = new TestObject();
        obj.setName("test-name");
        obj.setTestDate(new Date());

        ModelMap model = new ModelMap();
        model.addAttribute("object", obj);

        RequestContext rc = new RequestContext(request, model);

        BindStatus bindStatus = rc.getBindStatus("object.name");
        assertEquals("test-name", bindStatus.getValue());

        bindStatus = rc.getBindStatus("object.testDate");
        assertEquals(Date.class, bindStatus.getValue().getClass());

    }

    // Test bind result
    public void testBindResult() throws Exception
    {
        ApplicationContext ac = new MockWebApplicationContext();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute(RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);

        BindServiceImpl bindService = new BindServiceImpl();
        //bs.setDaoService(new MockDaoService(dao));

        ActionUtils actionUtils = new ActionUtils(request, response, bindService, validatorService);

       // TestObject t = new TestObject();
        BindingResult r = (BindingResult) actionUtils.bindAndValidate(TestObject.class.getName());

        request.setAttribute("org.springframework.validation.BindingResult.object", r);

        RequestContext rc = new RequestContext(request);

        // Name is required and is missing so should have an error
        BindStatus bindStatus = rc.getBindStatus("object.name");
        assertNotNull(bindStatus.getErrorMessage());

    }

    // Test bind result
    public void testBindResultDynaNode() throws Exception
    {
        ApplicationContext ac = new MockWebApplicationContext();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute(RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);

        BindServiceImpl bindService = new BindServiceImpl();
        //bs.setDaoService(new MockDaoService(dao));

        ActionUtils actionUtils = new ActionUtils(request, response, bindService, validatorService);

        BindingResult bindingResult = actionUtils.bindAndValidate("ArticlePage");
        request.setAttribute("org.springframework.validation.BindingResult.article", bindingResult);

        RequestContext rc = new OORequestContext(request, null);
        BindStatus bindStatus = rc.getBindStatus("article.data[title]");
        assertNotNull(bindStatus.getErrorMessage());
        System.out.println(bindStatus.getErrorMessage());

    }
}
