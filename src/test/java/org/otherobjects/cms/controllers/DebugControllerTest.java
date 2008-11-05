package org.otherobjects.cms.controllers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

public class DebugControllerTest extends TestCase
{
    public void testScript() throws Exception
    {
        DebugController debugController = new DebugController();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        ServletContext sc = new MockServletContext();
        debugController.setServletContext(sc);
        debugController.script(request, response);
    }
}
