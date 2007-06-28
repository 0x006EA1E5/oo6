package org.otherobjects.cms.jcr;

import java.io.IOException;

import org.apache.jackrabbit.ocm.mapper.impl.DigesterMapperImpl;
import org.springframework.core.io.Resource;

public class ResourceDigesterMappingImpl extends DigesterMapperImpl
{
    public ResourceDigesterMappingImpl(Resource resource) throws IOException
    {
        super(resource.getInputStream());
    }

}
