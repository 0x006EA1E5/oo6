package org.otherobjects.cms.views;

import java.lang.annotation.Annotation;

import junit.framework.TestCase;

import org.otherobjects.cms.tools.GravatarTool;
import org.otherobjects.cms.tools.NavigationTool;

public class FreemarkerToolProviderTest extends TestCase
{
    public void testGetTools()
    {
        Annotation[] annotations = GravatarTool.class.getAnnotations();
        assertTrue(annotations.length > 0);
    }

    public void testGenerateName()
    {
        FreemarkerToolProvider freemarkerToolProvider = new FreemarkerToolProvider();
        assertEquals("navigationTool", freemarkerToolProvider.generateName(NavigationTool.class.getName()));
    }
}
