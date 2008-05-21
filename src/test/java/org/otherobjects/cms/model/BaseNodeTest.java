package org.otherobjects.cms.model;

import junit.framework.TestCase;

import org.otherobjects.cms.jcr.SampleObject;
import org.otherobjects.cms.types.TypeDefImpl;

public class BaseNodeTest extends TestCase
{

    public void testSetJcrPath()
    {
        SampleObject node = new SampleObject();
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
        }
    }

    public void testGetCode()
    {
        SampleObject node = new SampleObject();
        
        // Check for null
        try
        {
            node.setCode(null);
            fail();
        }
        catch (RuntimeException e)
        {
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
        }

        
        // Test auto-generation
        node = new SampleObject();
        
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
        SampleObject n1 = new SampleObject();
        n1.set("name", "name1");

        assertEquals("name1", n1.get("name"));
        
        // FIXME Test nested properties
    }

}

