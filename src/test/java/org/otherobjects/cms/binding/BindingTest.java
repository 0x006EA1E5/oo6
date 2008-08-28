package org.otherobjects.cms.binding;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.model.Comment;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefBuilder;
import org.otherobjects.cms.types.TypeDefImpl;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.TypeServiceImpl;
import org.otherobjects.cms.util.ActionUtils;
import org.otherobjects.cms.validation.BaseNodeValidator;
import org.otherobjects.cms.validation.ValidatorServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;

public class BindingTest extends TestCase
{
    private static final String COMMENT = "test";

    private TypeService typeService = new TypeServiceImpl();

    @Override
    protected void setUp() throws Exception
    {
        // Setup type service
        super.setUp();
        SingletonBeanLocator.registerTestBean("typeService", typeService);
        TypeDefBuilder typeDefBuilder = new AnnotationBasedTypeDefBuilder();
        typeService.registerType(typeDefBuilder.getTypeDef(Comment.class));
        ((TypeServiceImpl) typeService).reset();

        // Add DynaNode type
        TypeDefImpl td = new TypeDefImpl("ArticlePage");
        td.setLabelProperty("title");
        td.addProperty(new PropertyDefImpl("title", "string", null, null, true));
        typeService.registerType(td);

    }

    // Test new form
    public void testBindNew()
    {
        ApplicationContext ac = new MockWebApplicationContext();
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);

        // Bean
        Comment c = new Comment();
        ModelMap model = new ModelMap();
        model.addAttribute("comment", c);
        RequestContext rc = new RequestContext(request, model);
        BindStatus bindStatus = rc.getBindStatus("comment.comment");
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

        Comment c = new Comment();
        c.setComment(COMMENT);

        ModelMap model = new ModelMap();
        model.addAttribute("comment", c);

        RequestContext rc = new RequestContext(request, model);
        BindStatus bindStatus = rc.getBindStatus("comment.comment");

        assertEquals(COMMENT, bindStatus.getValue());

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

        ValidatorServiceImpl validatorService = new ValidatorServiceImpl();
        validatorService.setBaseNodeValidator(new BaseNodeValidator(typeService));
        ActionUtils actionUtils = new ActionUtils(request, response, bindService, validatorService);

        Comment c = new Comment();
        BindingResult r = (BindingResult) actionUtils.bindAndValidate(Comment.class.getName());

        request.setAttribute("org.springframework.validation.BindingResult.comment", r);

        RequestContext rc = new RequestContext(request);

        BindStatus bindStatus = rc.getBindStatus("comment.comment");

        assertNotNull(bindStatus.getErrorMessage());
        System.out.println(bindStatus.getErrorMessage());

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

        ValidatorServiceImpl validatorService = new ValidatorServiceImpl();
        validatorService.setBaseNodeValidator(new BaseNodeValidator(typeService));
        ActionUtils actionUtils = new ActionUtils(request, response, bindService, validatorService);

        BindingResult bindingResult = actionUtils.bindAndValidate("ArticlePage");
        request.setAttribute("org.springframework.validation.BindingResult.article", bindingResult);

        RequestContext rc = new OORequestContext(request, null);
        BindStatus bindStatus = rc.getBindStatus("article.title");
        assertNotNull(bindStatus.getErrorMessage());
        System.out.println(bindStatus.getErrorMessage());

    }

}
