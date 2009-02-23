package org.otherobjects.cms.model;

import junit.framework.TestCase;

import org.otherobjects.cms.binding.TestObject;
import org.otherobjects.cms.types.TypeDefImpl;

public class BaseNodeTest extends TestCase
{

    public void testSetJcrPath()
    {
        TestObject node = new TestObject();
        node.setJcrPath("/folder/code.html");
        assertEquals("/folder/", node.getPath());
        assertEquals("code.html", node.getCode());

        // Check for null
        try
        {
            node.setJcrPath(null);
            fail();
        }
        catch (RuntimeException e)
        {
            // TODO Explain why we ignore exception
        }
    }

    public void testGetCode()
    {
        TestObject node = new TestObject();

        // Check for null
        try
        {
            node.setCode(null);
            fail();
        }
        catch (RuntimeException e)
        {
            // TODO Explain why we ignore exception
        }

        // Check normal values
        node.setCode("code");
        assertEquals("code", node.getCode());

        // Check no slashes
        try
        {
            node.setCode("code/bad");
            fail();
        }
        catch (RuntimeException e)
        {
            // TODO Explain why we ignore exception
        }

        // Test auto-generation
        node = new TestObject();

        TypeDefImpl td = new TypeDefImpl();
        td.setLabelProperty("name");
        node.setTypeDef(td);

        node.setOoLabel("Hey! Here is a (really) bad url.");
        assertEquals("hey-here-is-a-really-bad-url", node.getCode());

        node.setCode("my-code");
        assertEquals("my-code", node.getCode());
    }

    public void testGet()
    {
        TestObject n1 = new TestObject();
        n1.setPropertyValue("name", "name1");

        assertEquals("name1", n1.getPropertyValue("name"));

        // FIXME Test nested properties
    }

}
