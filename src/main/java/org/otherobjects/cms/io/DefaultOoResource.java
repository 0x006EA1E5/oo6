package org.otherobjects.cms.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;

/**
 * Custom specialisation of Spring's {@link Resource} abstraction. Should always be obtained via {@link OoResourceLoader} bean.
 * 
 * @author joerg
 *
 */
public class DefaultOoResource extends AbstractResource implements OoResource
{
    private Resource resource;
    private String path;
    private boolean writable;
    private OoResourceMetaData metaData;

    public OoResourceMetaData getMetaData()
    {
        return metaData;
    }

    public void setMetaData(OoResourceMetaData metaData)
    {
        this.metaData = metaData;
    }

    public DefaultOoResource(Resource resource, String path, boolean writable)
    {
        this.resource = resource;
        this.path = path;
        this.writable = writable;
    }

    public String getDescription()
    {
        return resource.getDescription();
    }

    public InputStream getInputStream() throws IOException
    {
        return resource.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException
    {
        if (!writable)
        {
            throw new IOException("This resource is not writable!");
        }
        else
        {
            return new FileOutputStream(resource.getFile());
        }

    }

    @Override
    public URL getURL() throws IOException
    {
        return resource.getURL();
    }

    @Override
    public File getFile() throws IOException
    {
        return resource.getFile();
    }

    @Override
    public String getFilename() throws IllegalStateException
    {
        return resource.getFilename();
    }

    public String getPath()
    {
        return path;
    }
    
    /**
     * FIXME Don't like the name of this method. Or even if it works for enought resources to make it worth file.  
     * FIXME Actually might be better to just return URL instead.
     * 
     * @return
     */
    public String getFilePath()
    {
        try
        {
            String file = resource.getURI().toString();
            if(file.startsWith("file"))
                return file.substring(5);
            else 
                return null;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public boolean isWritable()
    {
        return writable;
    }

}
