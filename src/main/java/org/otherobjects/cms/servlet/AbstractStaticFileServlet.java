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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet returns a static file.
 * 
 * Classes extending this class need to do two things:
 * <ul>
 *  <li>they need to override the init method and determine the file system root path to be used for 
 *  a particular implementation</li>
 *  <li>they need to override {@link #determineRelativePath(HttpServletRequest)} to decide which part 
 *  of the incoming request is to be used to find the file starting at rootPath</li>
 * </ul>
 * 
 * FIXME Politer warnings and messages
 * FIXME Merge in performance improvements from http://balusc.blogspot.com/2009/02/fileservlet-supporting-resume-and.html
 * 
 * Based on: org.jruby.webapp.FileServlet. 
 *
 * @author Robert Egglestone
 */
public abstract class AbstractStaticFileServlet extends HttpServlet
{
    private static final long serialVersionUID = -1850176862525216468L;

    protected final Logger logger = LoggerFactory.getLogger(AbstractStaticFileServlet.class);

    protected static final long DEFAULT_CACHE_TIME_MS = 15 * 60 * 1000;
    protected int bufferSize = 4096;
    protected String basePath;

    @Override
    public void init() throws ServletException
    {
        super.init();
    }

    abstract protected String determineRelativePath(HttpServletRequest request);

    @Override
    protected long getLastModified(HttpServletRequest request) {
        String relativePath = determineRelativePath(request);
        String realPath = basePath + relativePath;
        try
        {
            realPath = URLDecoder.decode(realPath, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.warn("Failed to UTF-8 decode file path '{}'", realPath, e);
        }
        File file = new File(realPath).getAbsoluteFile();
        if (file.isFile())
        {
            return file.lastModified();
        }
        return super.getLastModified(request);
    }
    
    /**
     * Transfer the file.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String relativePath = determineRelativePath(request);
        ReadableByteChannel in = null;
        try
        {
            in = getInputChannel(relativePath);
            sendFile(relativePath, in, response);
        }
        catch (NotModifiedException e){
            response.setContentLength(0);
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            logger.info("Static resource not modified: {}", relativePath);
        }
        catch (FileNotFoundException e){
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            logger.warn("Static resource not found: {}", relativePath);
        }
        catch (NullPointerException e){
            // TODO Is there a better way to catch a non-existent file?
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            logger.error("Static resource not found: {}", relativePath);
        }
        catch (IOException e){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            logger.error("Error serving static resource request: {}", relativePath, e);
        }
        finally{
            try{
                if (in != null)
                    in.close();
            }
            catch (Exception ignore){} // Ignore exception
        }
    }
    
    protected ReadableByteChannel getInputChannel(String relativePath) throws FileNotFoundException
    {
        String realPath = basePath + relativePath;
        try
        {
            realPath = URLDecoder.decode(realPath, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.warn("Failed to UTF-8 decode file path '{}'", realPath, e);
        }
        File file = new File(realPath).getAbsoluteFile();
        if (!file.isFile())
        {
            log("File not found: " + realPath);
            throw new FileNotFoundException(realPath);
        }
        
        return new FileInputStream(file).getChannel();
    }
    
    protected void sendFile(String relativePath, ReadableByteChannel in, HttpServletResponse response) throws ServletException, IOException
    {
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

        logger.info("Static resource served: {}", relativePath);

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
        // Get content type by file name.
        String contentType = getServletContext().getMimeType(fileName);

        // If content type is unknown, then set the default value.
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        // To add new content types, add new mime-mapping entry in web.xml.
        if (contentType == null)
            contentType = "application/octet-stream";

        return contentType;
    }

    /**
     * An exception when the source object has not been modified.
     * While this condition is not a failure, it is a break from the normal flow of execution.
     */
    @Deprecated
    protected static class NotModifiedException extends IOException
    {
        private static final long serialVersionUID = -4727191583159610374L;

        public NotModifiedException()
        {
        }
    }

}
