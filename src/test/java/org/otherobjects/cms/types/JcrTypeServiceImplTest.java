package org.otherobjects.cms.types;

import org.otherobjects.cms.test.BaseJcrTestCase;

public class JcrTypeServiceImplTest extends BaseJcrTestCase
{
    /* Autowired */
    private TypeService typeService;

    public void testLoadTypes()
    {
        assertNotNull(typeService);
        assertEquals(2, typeService.getTypes().size());
        
        TypeDef folder = typeService.getType("Folder");
        assertNotNull(folder);
        assertNotNull(folder.getProperty("name"));
    }

    public void testGetType()
    {

        //        TypeService types = TypeService.getInstance();
        //        TypeDef type = types.getType("oo_TypeDef");
        //        assertNotNull(type);
        //        assertEquals("oo_TypeDef", type.getName());
    }

    public TypeService getTypeService()
    {
        return typeService;
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

}
