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

    public void testGetProperty()
    {
        CmsNode n2 = new CmsNode();
        n2.set("p1", "n2p1");

        CmsNode n1 = new CmsNode();
        n1.set("p1", "n1p1");
        n1.set("n2", n2);

        assertEquals(n1.get("p1"), n1.getProperty("p1"));
        assertEquals(n2.get("p1"), n1.getProperty("n2.p1"));
    }

}
