package org.otherobjects.cms.views;

import java.lang.annotation.Annotation;

import org.otherobjects.cms.tools.GravatarUtils;

import junit.framework.TestCase;

public class FreemarkerToolProviderTest extends TestCase
{

    public void testGetTools()
    {
        Annotation[] annotations = GravatarUtils.class.getAnnotations();
        assertTrue(annotations.length > 0);
    }

}
