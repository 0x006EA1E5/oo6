package org.otherobjects.cms.views;

import java.lang.annotation.Annotation;

import junit.framework.TestCase;

import org.otherobjects.cms.tools.GravatarTool;
import org.otherobjects.cms.tools.NavigationTool;

public class FreeMarkerToolProviderTest extends TestCase
{
    public void testGetTools()
    {
        Annotation[] annotations = GravatarTool.class.getAnnotations();
        assertTrue(annotations.length > 0);
    }

    public void testGenerateName()
    {
        FreeMarkerToolProvider freeMarkerToolProvider = new FreeMarkerToolProvider();
        assertEquals("navigationTool", freeMarkerToolProvider.generateName(NavigationTool.class.getName()));
    }
}
