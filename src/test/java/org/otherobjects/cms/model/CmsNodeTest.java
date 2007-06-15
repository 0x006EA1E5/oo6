package org.otherobjects.cms.model;

import junit.framework.TestCase;

public class CmsNodeTest extends TestCase
{

    public void testSetJcrPath()
    {
        CmsNode node = new CmsNode();
        
        node.setJcrPath("/folder/code.html");
        assertEquals("/folder/", node.getPath());
        assertEquals("code.html", node.getCode());
        
    }

}
