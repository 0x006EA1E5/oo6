package org.otherobjects.cms.rest;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.otherobjects.cms.rest.IdFromUrlFilter.IdFromUrlRequest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class IdFromUrlFilterTest extends TestCase
{
    public void testFilter() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        IdFromUrlFilter filter = new IdFromUrlFilter();

        request.addParameter("id", "1");
        request.setPathInfo("/controller/action/123");

        filter.doFilter(request, response, chain);

        HttpServletRequest result = (HttpServletRequest) chain.getRequest();
        assertTrue(result.getParameter("id").equals("1"));
        assertTrue(result.getParameter("resourceId").equals("123"));

        assertTrue(result instanceof IdFromUrlRequest);

        request = new MockHttpServletRequest();
        chain = new MockFilterChain();
        request.addParameter("id", "2");

        request.setPathInfo("/controller/test");

        filter.doFilter(request, response, chain);

        result = (HttpServletRequest) chain.getRequest();
        System.out.println(result.getParameter("id"));
        assertTrue(result.getParameter("id").equals("2"));
        assertFalse(result instanceof IdFromUrlRequest);
        assertNull(result.getParameter("resourceId"));

        request = new MockHttpServletRequest();
        chain = new MockFilterChain();
        request.addParameter("id", "3");
        request.addParameter("id", "4");

        request.setPathInfo("/controller/test/345");

        filter.doFilter(request, response, chain);

        result = (HttpServletRequest) chain.getRequest();

        assertTrue(result.getParameterValues("id")[0].equals("3"));
        assertTrue(result instanceof IdFromUrlRequest);
        assertTrue(result.getParameter("resourceId").equals("345"));

    }
}
