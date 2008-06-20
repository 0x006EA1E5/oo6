package org.otherobjects.cms.model;

import junit.framework.TestCase;

public class SelectorTest extends TestCase
{
    public void testGetQuery()
    {
        Selector s = new Selector();
        s.setQueryPath("/site/news/");
        assertEquals("/jcr:root/site/news/*", s.getQuery());
        s.setRecursive(true);
        assertEquals("/jcr:root/site/news//*", s.getQuery());
    }
}
