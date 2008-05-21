package org.otherobjects.cms.util;

import junit.framework.TestCase;

import org.springframework.core.io.FileSystemResource;

public class ResourceCleanerTest extends TestCase
{
    public void testClean()
    {
        ResourceCleaner rc = new ResourceCleaner();
        rc.setResource(new FileSystemResource("./target/data"));
        rc.cleanup();
    }
}
