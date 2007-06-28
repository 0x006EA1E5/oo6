package org.otherobjects.cms.model;

import junit.framework.TestCase;

public class DynaNodeTest extends TestCase
{

    public void testSetJcrPath()
    {
        DynaNode node = new DynaNode();

        node.setJcrPath("/folder/code.html");
        assertEquals("/folder/", node.getPath());
        assertEquals("code.html", node.getCode());

    }

    public void testGetProperty()
    {
        DynaNode n2 = new DynaNode();
        n2.set("p1", "n2p1");

        DynaNode n1 = new DynaNode();
        n1.set("p1", "n1p1");
        n1.set("n2", n2);

        assertEquals(n1.get("p1"), n1.getProperty("p1"));
        assertEquals(n2.get("p1"), n1.getProperty("n2.p1"));
    }

}
