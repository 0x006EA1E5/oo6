package org.otherobjects.cms.util;

import org.apache.tools.ant.taskdefs.Delete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class ResourceCleaner
{
    private final Logger logger = LoggerFactory.getLogger(ResourceCleaner.class);

    private Resource resource;

    public void setResource(Resource resource)
    {
        this.resource = resource;
    }

    public void cleanup()
    {
        try
        {
            Delete delete = new Delete();
            if (resource.getFile().isDirectory())
                delete.setDir(resource.getFile());
            else
                delete.setFile(resource.getFile());

            delete.execute();

        }
        catch (Exception e)
        {
            logger.debug("Cleanup of resource " + resource.toString() + " wasn't successfull", e);
        }
    }
}
