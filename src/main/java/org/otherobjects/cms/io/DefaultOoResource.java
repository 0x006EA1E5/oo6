package org.otherobjects.cms.io;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;

public class DefaultOoResource extends AbstractResource implements OoResource
{
    private Resource resource;

    public DefaultOoResource(Resource resource)
    {
        this.resource = resource;
    }

    public String getDescription()
    {
        return resource.getDescription();
    }

    public InputStream getInputStream() throws IOException
    {
        return resource.getInputStream();
    }

}
