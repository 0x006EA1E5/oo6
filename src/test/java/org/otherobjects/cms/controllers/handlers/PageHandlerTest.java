package org.otherobjects.cms.controllers.handlers;

import org.otherobjects.cms.jcr.BaseJcrTestCase;
import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.model.TemplateLayout;

public class PageHandlerTest extends BaseJcrTestCase
{
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        //        SimpleNamingContextBuilder simpleNamingContextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        //        String serverName = "127.0.0.1";
        //        String contextPath = "test";
        //        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_SERVER_NAME_PATH, serverName);
        //        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_CONTEXT_PATH_PATH, contextPath);
        registerType(TestPage.class);
        anoymousLogin();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        logout();
    }

    public void testHandleRequest() throws Exception
    {
        //        MockHttpServletRequest request = new MockHttpServletRequest();
        //        MockHttpServletResponse response = new MockHttpServletResponse();

        Template template = new Template();
        TemplateLayout templateLayout = new TemplateLayout();

        templateLayout.setCode("TEST");
        template.setLayout(templateLayout);

        TestPage resource = new TestPage();
        resource.setTemplate(template);

        //FIXME we need a proper siteNavigatorService backed resource so that the siteTrail can be calculated properly when PageHandler populated the ctx with siteTrail
        //        ModelAndView mav = pageHandler.handleRequest(resource, request, response);
        //
        //        assertEquals("/site.resources/templates/layouts/TEST", mav.getViewName());
        //
        //        assertEquals(resource, mav.getModelMap().get("resourceObject"));

    }
}
