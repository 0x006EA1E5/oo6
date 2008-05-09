package org.otherobjects.cms.io;

import junit.framework.TestCase;

public class OoResourcePathPrefixTest extends TestCase
{
    public void testPattern()
    {
        String path = "site/some/path";

        assertTrue(OoResourcePathPrefix.SITE.pattern().matcher(path).lookingAt());
        System.out.println(OoResourcePathPrefix.SITE.pattern().matcher(path).replaceFirst(""));
    }
}
