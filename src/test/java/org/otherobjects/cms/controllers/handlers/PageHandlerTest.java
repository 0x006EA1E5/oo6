package org.otherobjects.cms.controllers.handlers;

import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.model.TemplateLayout;
import org.otherobjects.cms.test.BaseJcrTestCase;

public class PageHandlerTest extends BaseJcrTestCase
{
    @Override
    protected String[] getConfigLocations()
    {
        setAutowireMode(AUTOWIRE_BY_TYPE);
        return new String[]{"file:src/test/resources/applicationContext-resources.xml", "file:src/test/resources/applicationContext-repository.xml",
                "file:src/main/resources/otherobjects.resources/config/applicationContext-otherobjects.xml", "file:src/test/resources/applicationContext-messages.xml"};

        //        setAutowireMode(AUTOWIRE_BY_TYPE);
        //        //setAutowireMode(AUTOWIRE_BY_NAME);
        //        return new String[]{"file:src/test/resources/applicationContext-resources.xml", "file:src/test/resources/applicationContext-repository.xml"};//,"file:src/main/resources/otherobjects.resources/config/applicationContext-dao.xml"};
    }

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        //        SimpleNamingContextBuilder simpleNamingContextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        //        String serverName = "127.0.0.1";
        //        String contextPath = "test";
        //        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_SERVER_NAME_PATH, serverName);
        //        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_CONTEXT_PATH_PATH, contextPath);
        registerType(TestPage.class);
        anoymousLogin();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        super.onTearDown();
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
