package org.otherobjects.cms.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility methods for working with request data.
 * 
 * @author rich
 */
public class RequestUtils
{
    private static final String XHR_REQUEST_HEADER_KEY = "XMLHttpRequest";
    private static final String XHR_REQUEST_HEADER_VALUE = "X-Requested-With";

    /**
     * Returns the last part of the path info (after the last slash).
     * 
     * @param request
     * @return
     */
    public static String getId(HttpServletRequest request)
    {
        String pathInfo = request.getPathInfo();
        return pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
    }

    /**
     * Returns the last part of the path info after the method string.
     * 
     * @param method
     * @param request
     * @return
     */
    public static String getId(String method, HttpServletRequest request)
    {
        String pathInfo = request.getPathInfo();
        int startPos = pathInfo.indexOf("/" + method + "/") + method.length() + 2;
        return pathInfo.substring(startPos);
    }

    /**
     * Returns true if this request is generated from XHR.
     * 
     * @param request
     * @return
     */
    public static boolean isXhr(HttpServletRequest request)
    {
        String requestedWith = request.getHeader(XHR_REQUEST_HEADER_VALUE);
        return StringUtils.equals(requestedWith,XHR_REQUEST_HEADER_KEY);
    }
}
