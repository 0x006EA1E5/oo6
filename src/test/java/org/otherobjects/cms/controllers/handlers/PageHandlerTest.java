package org.otherobjects.cms.controllers.handlers;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.model.TemplateLayout;
import org.otherobjects.cms.test.BaseJcrTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

public class PageHandlerTest extends BaseJcrTestCase
{

    private DaoService daoService;

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
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
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        PageHandler pageHandler = new PageHandler();
        pageHandler.setDaoService(this.daoService);

        Template template = new Template();
        TemplateLayout templateLayout = new TemplateLayout();

        templateLayout.setCode("TEST");
        template.setLayout(templateLayout);

        TestPage resource = new TestPage();
        resource.setTemplate(template);
        ModelAndView mav = pageHandler.handleRequest(resource, request, response);

        assertEquals("/site.resources/templates/layouts/TEST", mav.getViewName());

        assertEquals(resource, mav.getModelMap().get("resourceObject"));

    }
}
