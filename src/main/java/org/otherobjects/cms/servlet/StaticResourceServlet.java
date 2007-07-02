package org.otherobjects.cms.servlet;

import javax.servlet.ServletContext;

import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.resource.Resource;

/**
 * Fast servlet to serve static resources for the webapp. Static resources
 * may come from local project files or the classpath (such as those provided
 * by otherobjects and other plugins).
 * 
 * <p>TODO Check caching and gzip
 * <br>TODO Local file serving
 * <br>TODO Check content types
 * <br>TODO Adapt to mapped path correctly
 * <br>TODO Check this works in non-Jetty containers
 * 
 * @author rich
 */
public class StaticResourceServlet extends DefaultServlet
{
    private static final long serialVersionUID = 3970455238515527584L;

    @Override
    public Resource getResource(String pathInContext)
    {
        if (pathInContext == null)
            return null;

        // FIXME Security check: so that configuration data can't be served
        String path = pathInContext.substring("resources".length() + 2);

        if (!path.startsWith("static/") && !path.startsWith("templates/"))
            return null;

        return Resource.newClassPathResource(path);
    }
}
