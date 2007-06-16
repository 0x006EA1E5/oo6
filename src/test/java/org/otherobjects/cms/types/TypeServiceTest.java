package org.otherobjects.cms.types;

import junit.framework.TestCase;

public class TypeServiceTest extends TestCase
{
    public void testTypeService()
    {
        TypeService.getInstance().reset();
        TypeService types = TypeService.getInstance();
        assertEquals(2, types.getTypes().size());
    }

    public void testGetType()
    {
        TypeService types = TypeService.getInstance();
        TypeDef type = types.getType("oo_TypeDef");
        assertNotNull(type);
        assertEquals("oo_TypeDef", type.getName());
    }

}
