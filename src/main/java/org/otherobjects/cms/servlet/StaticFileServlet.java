package org.otherobjects.cms.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import javax.activation.FileTypeMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet returns a static file.
 * 
 * FIXME Make an abstractStaticServlet
 * FIXME Politer warnings and messages
 * 
 * Based on: org.jruby.webapp.FileServlet. 
 *
 * @author Robert Egglestone
 */
public class StaticFileServlet extends HttpServlet
{
    private static final long DEFAULT_CACHE_TIME_MS = 15 * 60 * 1000; // 15 minutes

    private final Logger logger = LoggerFactory.getLogger(StaticFileServlet.class);

    private static final long serialVersionUID = -1850176862525216468L;
    private int bufferSize = 4096; // Increase buffer size from 1024
    private String rootPath;

    /**
     * Initialize the servlet, and determine the webapp root.
     */
    public void init() throws ServletException
    {
        // determine the root of this webapp
        ServletContext context = getServletContext();
        rootPath = context.getRealPath("/");
        if (rootPath == null)
        {
            throw new ServletException("Cannot find the real path of this webapp, probably using a non-extracted WAR");
        }
        if (rootPath.endsWith("/"))
        {
            rootPath = rootPath.substring(0, rootPath.length() - 1);
        }
    }

    /**
     * Transfer the file.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        ReadableByteChannel in = null;
        try
        {

            // find the location of the file
            String contextPath = request.getContextPath();
            String relativePath = request.getRequestURI().substring(contextPath.length());
            logger.info("Static resource request: {}", relativePath);

            in = getInputChannel(relativePath, request);

            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            // start returning the response
            response.setContentType(getContentTypeFor(relativePath));
            // FIXME Cache content for 15 minutes by default
            response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_CACHE_TIME_MS);
            OutputStream out = response.getOutputStream();

            // read the bytes, returning them in the response
            while (in.read(buffer) != -1)
            {
                out.write(buffer.array(), 0, buffer.position());
                buffer.clear();
            }
            out.close();

        }
        catch (NotModifiedException e)
        {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        catch (FileNotFoundException e)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (IOException e)
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        finally
        {
            try
            {
                if (in != null)
                    in.close();
            }
            catch (IOException ignore)
            {
                // Ignore exception
            }
        }
    }

    protected ReadableByteChannel getInputChannel(String relativePath, HttpServletRequest request) throws FileNotFoundException, NotModifiedException
    {
        // check the file and open it
        String realPath = rootPath + relativePath;
        try
        {
            realPath = URLDecoder.decode(realPath, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        File file = new File(realPath);
        if (!file.isFile())
        {
            log("File not found: " + realPath);
            throw new FileNotFoundException(realPath);
        }

        // check for modifications
        //        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        //        long lastModified = file.lastModified();
        //        if (ifModifiedSince != -1 && lastModified < ifModifiedSince)
        //        {
        //            throw new NotModifiedException();
        //        }

        // setup IO streams
        return new FileInputStream(file).getChannel();
    }

    /**
     * Static files treat GET and POST requests the same way.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

    /**
     * Return the content-type the would be returned for this file name.
     */
    public String getContentTypeFor(String fileName)
    {
        // FIXME Move these to mime.types
        // quick hack for types that are necessary, but not handled
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".css"))
        {
            return "text/css";
        }
        else if (lowerName.endsWith(".xml"))
        {
            return "text/xml";
        }
        else if (lowerName.endsWith(".js"))
        {
            return "text/javascript";
        }
        // everything else
        FileTypeMap typeMap = FileTypeMap.getDefaultFileTypeMap();
        return typeMap.getContentType(fileName);
    }

    /** 
     * Return last modified date for this resource.
     * 
     * <p>FIXME Implement this
     */
    //    public long getLastModified(HttpServletRequest req) {
    //        return dataModified.getTime(  ) / 1000 * 1000;
    //      }
    /**
     * An exception when the source object has not been modified.
     * While this condition is not a failure, it is a break from the normal flow of execution.
     */
    protected static class NotModifiedException extends IOException
    {
        private static final long serialVersionUID = -4727191583159610374L;

        public NotModifiedException()
        {
        }
    }
}
