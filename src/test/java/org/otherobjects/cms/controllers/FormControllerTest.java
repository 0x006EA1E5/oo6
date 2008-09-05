package org.otherobjects.cms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.otherobjects.cms.binding.MockWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.support.RequestContext;

public class FormControllerTest extends TestCase
{
    public void testHandleRequest() throws Exception
    {
        try
        {
            HttpServletRequest request = new MockHttpServletRequest();

            ApplicationContext ac = new MockWebApplicationContext();
            request.setAttribute(RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);
      
            FormController formController = new FormController();
            HttpServletResponse response = new MockHttpServletResponse();
            formController.handleRequest(request, response);
        }
        catch (Exception e)
        {
        }
    }
}
