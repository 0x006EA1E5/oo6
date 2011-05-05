package org.otherobjects.cms.servlet;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.context.support.WebApplicationContextUtils;


public class DataFileServlet extends AbstractStaticFileServlet
{
    private static final long serialVersionUID = 145409902479591179L;

    /**
     * Initialize the servlet, and determine the webapp root.
     */
    public void init() throws ServletException
    {
        // get applicationContext
        ApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        OtherObjectsConfigurator config = (OtherObjectsConfigurator) appContext.getBean("otherObjectsConfigurator");
        basePath = config.getProperty("site.public.data.path");
        
        Assert.isTrue(StringUtils.isNotBlank(basePath), "site.public.data.path property is not set. Webapp can't be used without that.");
        
        if (basePath.endsWith("/"))
        {
            basePath = basePath.substring(0, basePath.length() - 1);
        }
    }

    @Override
    protected String determineRelativePath(HttpServletRequest request)
    {
        String contextPath = request.getContextPath();
        return request.getPathInfo().substring(contextPath.length());
    }
}
