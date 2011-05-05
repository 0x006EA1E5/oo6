package org.otherobjects.cms.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * Fast servlet to serve static classpath resources for the webapp (such as 
 * those provided by OTHERobjects and other plugins).
 * 
 * <p>Note: this will only serve resources that are in a /static/ folder as basic
 * attempt to a stop access to all Classpath resources.
 * 
 * <p>Used primarily for serving static content from the OTHERobjects classpath.
 * 
 * @author rich
 */
public class StaticClasspathServlet extends AbstractStaticFileServlet
{
    private final Logger logger = LoggerFactory.getLogger(StaticClasspathServlet.class);
    
    private static final long serialVersionUID = -4839792818070350042L;

    @Override
    protected ReadableByteChannel getInputChannel(String path) throws FileNotFoundException
    {
        //  Security check: so that non-static data is not served
        if (!path.contains("/static/") || path.contains(".."))
        {
            this.logger.warn("Prevented access to non-static resource: {}", path);
            //resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            throw new FileNotFoundException();
        }

        InputStream in = getClass().getResourceAsStream(path);
        return Channels.newChannel(in);
    }

    @Override
    protected String determineRelativePath(HttpServletRequest request)
    {
        return request.getPathInfo();
    }
    
    /** Quickly determine if this resource has changed.<br/><br/>
     * 
     *  Try to find the file on disk and return modification time of that.
     *  If we fail, return app startup time: assume the resource is in a .jar, 
     *  so we can't get the file, but also that the resource can't change between deploys)
     *
     * <hr/>
     * Date: 9 Jun 2010
     *
     * @see org.otherobjects.cms.servlet.AbstractStaticFileServlet#getLastModified(javax.servlet.http.HttpServletRequest)
     * @param request
     * @return
     * @author geales
     */
    @Override
    protected long getLastModified(HttpServletRequest request) 
    {
        String relativePath = determineRelativePath(request);
        String filePath = getClass().getResource(relativePath).getFile();
        if(filePath != null)
        {
            File file = new File(filePath);
            if(file.isFile())
            {
                logger.debug("Returning last modified date from file");
                return file.lastModified();
            }
        }
        
        WebApplicationContext wac = (WebApplicationContext) getServletContext().getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
        if(wac != null)
        {
            logger.debug("Returning last modified date from application startup time");
            return wac.getStartupDate();
        }
        return super.getLastModified(request);
    }
}
