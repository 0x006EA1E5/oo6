package org.otherobjects.cms.util;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;

public class RequestUtilsTest extends TestCase
{

    public void testGetId()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setPathInfo("/part/part/id");
        assertEquals("id",RequestUtils.getId(request));
        
        request.setPathInfo("/part/part/id");
        assertEquals("id",RequestUtils.getId(request));
    }

    public void testIsXhr()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertFalse(RequestUtils.isXhr(request));
        request.addHeader("X-Requested-With", "XMLHttpRequest");
        assertTrue(RequestUtils.isXhr(request));

        //        public static String getId(HttpServletRequest request)
        //        {
        //            String pathInfo = request.getPathInfo();
        //            return pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
        //        }
        //
        //        /**
        //         * Returns the last part of the path info after the method string.
        //         * 
        //         * @param method
        //         * @param request
        //         * @return
        //         */
        //        public static String getId(String method, HttpServletRequest request)
        //        {
        //            String pathInfo = request.getPathInfo();
        //            int startPos = pathInfo.indexOf("/" + method + "/") + method.length() + 2;
        //            return pathInfo.substring(startPos);
        //        }
        //
        //        /**
        //         * Returns true if this request is generated from XHR.
        //         * 
        //         * @param request
        //         * @return
        //         */
        //        public static boolean isXhr(HttpServletRequest request)
        //        {
        //            String requestedWith = request.getHeader(XHR_REQUEST_HEADER_VALUE);
        //            return StringUtils.equals(requestedWith,XHR_REQUEST_HEADER_KEY);
        //        }

    }

}
