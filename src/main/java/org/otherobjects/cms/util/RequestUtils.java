package org.otherobjects.cms.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility methods for working with request data.
 * 
 * @author rich
 */
public class RequestUtils
{

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

}
