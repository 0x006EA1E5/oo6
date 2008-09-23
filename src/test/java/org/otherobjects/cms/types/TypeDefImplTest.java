package org.otherobjects.cms.types;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TypeDefImplTest extends TestCase
{
    public void testSetProperties()
    {
        TypeDefImpl td = new TypeDefImpl("Test");
        List<PropertyDef> properties = new ArrayList<PropertyDef>();
        properties.add(new PropertyDefImpl("name", "string", null, null));
        td.setProperties(properties);
        assertNotNull(td.getProperty("name"));
    }

    public void testAddProperty()
    {
        TypeDefImpl td = new TypeDefImpl("Test");
        td.addProperty(new PropertyDefImpl("name", "string", null, null));
        assertNotNull(td.getProperty("name"));

        // Duplicate checking
        try
        {
            td.addProperty(new PropertyDefImpl("name", "string", null, null));
            fail();
        }
        catch (Exception e)
        {
            // TODO Explain why we ignore exception
        }
    }

    public void testGetLabel()
    {
        TypeDefImpl td = new TypeDefImpl("org.test.types.TestType");
        td.setLabel("A custom label");
        assertEquals("A custom label", td.getLabel());

        // Test infered labels
        td.setLabel("");
        assertEquals("Test Type", td.getLabel());
        
        td.setName("TestType"); //  No package
        assertEquals("Test Type", td.getLabel());
    }
}
