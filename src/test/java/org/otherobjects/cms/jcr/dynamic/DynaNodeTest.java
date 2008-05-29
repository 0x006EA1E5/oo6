package org.otherobjects.cms.jcr.dynamic;

import org.otherobjects.cms.jcr.BaseJcrTestCase;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefImpl;

public class DynaNodeTest extends BaseJcrTestCase
{
    public void testWrite()
    {
        TypeDefImpl articleTypeDef = new TypeDefImpl();
        //articleTypeDef.setClassName("");
        articleTypeDef.setName("test.Article");
        articleTypeDef.addProperty(new PropertyDefImpl("title", "string", null, null, true));
        articleTypeDef.addProperty(new PropertyDefImpl("content", "text", null, null));
        articleTypeDef.setLabelProperty("title");

        // Type must be registered with type service
        registerType(articleTypeDef);

        DynaNode a1 = new DynaNode("test.Article");
        a1.setPath("/");
        a1.set("title", "A1 title");
        a1.set("content", "A1 content");

        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("DynaNode");
        assertNotNull("Generic DAO not found", dao);
        dao.save(a1, false);

        DynaNode a1r = (DynaNode) dao.get(a1.getId());
        assertEquals(a1.get("title"), a1r.get("title"));
        assertEquals(a1.get("content"), a1r.get("content"));
    }
}
