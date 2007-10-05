package org.otherobjects.cms.controllers.handlers;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.model.RedirectResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class RedirectHandlerTest extends TestCase {
	
	public void testRedirectPermanently()
	{
		MockHttpServletRequest  request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		RedirectResource redirect = new RedirectResource();
		
		String redirectUrl = "/redirect.html";
		
		redirect.setUrl(redirectUrl);
		redirect.setTemporary(false);
		
		RedirectHandler redirectHandler = new RedirectHandler();
		try {
			redirectHandler.handleRequest(redirect, request, response);
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatus());
		assertEquals(redirectUrl, response.getHeader("Location"));
	
	}
	
	public void testRedirectTemporarily()
	{
		MockHttpServletRequest  request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		RedirectResource redirect = new RedirectResource();
		
		String redirectUrl = "/redirect.html";
		
		redirect.setUrl(redirectUrl);
		redirect.setTemporary(true);
		
		RedirectHandler redirectHandler = new RedirectHandler();
		try {
			redirectHandler.handleRequest(redirect, request, response);
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());
		assertEquals(redirectUrl, response.getHeader("Location"));
	
	}
	
	public void testRedirectWrongResource()
	{
		MockHttpServletRequest  request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		DynaNode redirect = new DynaNode();
		
				
		RedirectHandler redirectHandler = new RedirectHandler();
		try {
			redirectHandler.handleRequest(redirect, request, response);
			fail();
		} catch (Exception e) {
			assertEquals("java.lang.IllegalArgumentException", e.getClass().getName());
		}

	
	}
	
	public void testRedirectNullResource()
	{
		MockHttpServletRequest  request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		DynaNode redirect = null;
		
				
		RedirectHandler redirectHandler = new RedirectHandler();
		try {
			redirectHandler.handleRequest(redirect, request, response);
			fail();
		} catch (Exception e) {
			assertEquals("java.lang.IllegalArgumentException", e.getClass().getName());
		}

	
	}
	
	
}
