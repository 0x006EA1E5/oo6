package org.otherobjects.cms.io;

import java.io.IOException;

import junit.framework.TestCase;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class OoResourceLoaderTest extends TestCase
{

    public void testPreprocessPath()
    {
        String p1 = "/otherobjects/templates/pages/test.html";
        String p1r = "classpath:otherobjects.resources/templates/pages/test.html";

        OoResourceLoader ooResourceLoader = new OoResourceLoader();
        assertEquals(p1r, ooResourceLoader.preprocessPath(p1).getPath());
    }

    public void testGetResource() throws IOException
    {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        OoResourceLoader ooResourceLoader = new OoResourceLoader();
        ooResourceLoader.setResourceLoader(resourceLoader);
        
        String p1 = "/otherobjects/templates/hud/error-handling/oo-404-create.ftl";
        
        Resource resource = ooResourceLoader.getResource(p1);
        assertTrue(resource.exists());
    }
}
