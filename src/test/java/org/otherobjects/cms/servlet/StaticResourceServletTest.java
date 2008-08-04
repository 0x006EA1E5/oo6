package org.otherobjects.cms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.TestCase;

public class StaticResourceServletTest extends TestCase
{
    public void testDoGet() throws ServletException, IOException
    {
    	// FIXME Enable this test
//        StaticClasspathServlet staticResourceServlet = new StaticClasspathServlet();
//
//        MockHttpServletRequest req = new MockHttpServletRequest();
//        MockHttpServletResponse resp = new MockHttpServletResponse();
//        
        
        // Existing resource
        //req.setPathInfo("/otherobjects.resources/static/css/workbench.css");
        //staticResourceServlet.doGet(req, resp);
        //assertEquals(HttpServletResponse.SC_OK, resp.getStatus());
     
        // Missing resource
        //req.setPathInfo("/null.resources/static/missing.css");
        //staticResourceServlet.doGet(req, resp);
        //assertEquals(HttpServletResponse.SC_NOT_FOUND, resp.getStatus());
    }
}
