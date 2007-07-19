package org.otherobjects.cms.util;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase
{
    public void testGenerateLabel()
    {
        assertEquals("News Story", StringUtils.generateLabel("newsStory"));
    }

    public void testGenerateUrlCode()
    {
        assertEquals("news-story-new", StringUtils.generateUrlCode("News Story. New."));
    }
}
