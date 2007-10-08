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
        }
    }
}
