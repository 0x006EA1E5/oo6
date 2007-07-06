package org.otherobjects.cms.types;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TypeDefTest extends TestCase
{
    public void testSetProperties()
    {
        TypeDef td = new TypeDef("Test");
        List<PropertyDef> properties = new ArrayList<PropertyDef>();
        properties.add(new PropertyDef("name", "string", null, null));
        td.setProperties(properties);
        assertNotNull(td.getProperty("name"));
    }

    public void testAddProperty()
    {
        TypeDef td = new TypeDef("Test");
        td.addProperty(new PropertyDef("name", "string", null, null));
        assertNotNull(td.getProperty("name"));
        
        // Duplicate checking
        try
        {
            td.addProperty(new PropertyDef("name", "string", null, null));
            fail();
        }
        catch (Exception e)
        {
        }
    }
}
