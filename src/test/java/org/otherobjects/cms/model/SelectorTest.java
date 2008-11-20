package org.otherobjects.cms.model;

import junit.framework.TestCase;

public class SelectorTest extends TestCase
{
    public void testGetQuery()
    {
        Selector s = new Selector();
        s.setQueryPath("/site/news/");
        assertEquals("/jcr:root/site/news/*", s.getQuery());
        s.setSubFolders(true);
        assertEquals("/jcr:root/site/news//*", s.getQuery());
        s.setEnd(5L);
        assertEquals("/jcr:root/site/news//* {5}", s.getQuery());
        
        Selector s2 = new Selector();
        s2.setQueryTypeName("org.otherobjects.cms.model.Template");
        assertEquals("/jcr:root//* [@ooType='org.otherobjects.cms.model.Template']", s2.getQuery());
        s2.setQueryTypeName("%Folder");
        assertEquals("/jcr:root//* [jcr:like(@ooType,'%Folder')]", s2.getQuery());
        
    }
}
