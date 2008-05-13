package org.otherobjects.cms.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;

public class DefaultOoResource extends AbstractResource implements OoResource
{
    private Resource resource;
    private boolean writable;

    public DefaultOoResource(Resource resource, boolean writable)
    {
        this.resource = resource;
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

}
