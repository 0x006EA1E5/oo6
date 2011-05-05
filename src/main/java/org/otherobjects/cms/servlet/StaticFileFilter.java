package org.otherobjects.cms.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFileFilter implements Filter
{
    private final Logger logger = LoggerFactory.getLogger(StaticFileFilter.class);

    protected static final long DEFAULT_CACHE_TIME_MS = 1 * 60 * 1000; // FIXME Cache content for 15 minutes by default
    protected int bufferSize = 4096;
    protected String basePath;
    private ServletContext servletContext;

    public void init(FilterConfig config) throws ServletException
    {
        servletContext = config.getServletContext();
        basePath = config.getServletContext().getRealPath("");
        if (basePath.endsWith("/"))
            basePath = basePath.substring(0, basePath.length());
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws java.io.IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        String relativePath = httpRequest.getPathInfo();
        relativePath = URLDecoder.decode(relativePath, "UTF-8");
        String filePath = basePath + relativePath;
        File f = new File(filePath);

        // If file exists on disk then serve it up
        if (f.exists())
        {
            ReadableByteChannel in = new FileInputStream(f).getChannel();
            sendFile(relativePath, in, httpResp);
        }
        else
        {
            // Continue as normal
            chain.doFilter(request, response);
        }

    }

    public void destroy()
    {
    }

    protected void sendFile(String relativePath, ReadableByteChannel in, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            // start returning the response
            response.setContentType(getContentType(relativePath));

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
        catch (FileNotFoundException e)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            logger.error("Static resource not found: {}", relativePath);
        }
        catch (NullPointerException e)
        {
            // TODO Is there a better way to catch a non-existent file?
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            logger.error("Static resource not found: {}", relativePath);
        }
        catch (IOException e)
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            logger.error("Error serving static resource request: {}", relativePath, e);
        }
        finally
        {
            try
            {
                if (in != null)
                    in.close();
            }
            catch (Exception ignore)
            {
                // Ignore exception
            }
        }
    }

    /**
     * Return the content-type the would be returned for this file name.
     */
    public String getContentType(String fileName)
    {
        // Get content type by file name.
        String contentType = this.servletContext.getMimeType(fileName);

        // If content type is unknown, then set the default value.
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        // To add new content types, add new mime-mapping entry in web.xml.
        if (contentType == null)
            contentType = "application/octet-stream";

        return contentType;
    }
}
