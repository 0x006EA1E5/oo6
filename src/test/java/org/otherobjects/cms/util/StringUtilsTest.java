package org.otherobjects.cms.util;

import junit.framework.TestCase;

import org.otherobjects.cms.util.StringUtils;

public class StringUtilsTest extends TestCase
{
    public void testGenerateLabel()
    {
        assertEquals("News Story", StringUtils.generateLabel("newsStory"));
    }
}
