package org.otherobjects.cms.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fast servlet to serve static resources for the webapp. Static resources
 * may come from local project files or the classpath (such as those provided
 * by otherobjects and other plugins).
 * 
 * <p>TODO Check caching and gzip
 * <br>TODO Local file serving  - check not cached
 * <br>TODO Check content types
 * <br>TODO Adapt to mapped path correctly
 * <br>TODO Check this works in non-Jetty containers
 * <br>TODO Return string error messages to browser on failures
 * <br>FIXME Security checks for allowed files
 * @author rich
 */
public class StaticResourceServlet extends HttpServlet
{
    private static final int BUFFER_SIZE = 2048;

    private final Logger logger = LoggerFactory.getLogger(StaticResourceServlet.class);

    // FIXME We should unify this this DataFile mime type support
    private final MimeTypes mimeTypes = new MimeTypes();

    private static final long serialVersionUID = 3970455238515527584L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String path = req.getPathInfo();

        if (path == null)
            return;

        //  Security check: so that non-static data is not served
        if (!path.contains("/static/") && !path.contains(".."))
        {
            this.logger.warn("Prevented access to non-static resource: {}", path);
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // FIXME Is there a faster way of serving these?
        // FIXME Cache?
        InputStream in = getClass().getResourceAsStream(path);
        if (in == null)
        {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            this.logger.info("Static resource not found: {}", path);
            return;
        }

        // Add cache header
        resp.addHeader("Cache-Control", "max-age=3600");
        resp.setContentType(this.mimeTypes.getMimeByExtension(path).toString());
        resp.setCharacterEncoding("UTF-8");

        OutputStream out = resp.getOutputStream();
        try
        {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = buffer.length;
            while (true)
            {
                len = in.read(buffer);
                if (len == -1)
                    break;
                out.write(buffer, 0, len);
            }
            out.flush();
            this.logger.info("Served static resource: {}", path);
        }
        catch (Exception e)
        {
            this.logger.error("Error sending static resource: " + path, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        finally
        {
            if (in != null)
                in.close();
        }
    }

}
