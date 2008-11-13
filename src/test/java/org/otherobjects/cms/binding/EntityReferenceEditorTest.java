package org.otherobjects.cms.binding;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.otherobjects.cms.dao.MockDaoService;
import org.otherobjects.cms.dao.MockGenericDao;
import org.otherobjects.cms.model.Role;

public class EntityReferenceEditorTest extends TestCase
{

    public void testSetAsText()
    {
        Role role = new Role();
        role.setName("Test Role");
        Map<Serializable, Object> objects = new HashMap<Serializable, Object>();
        objects.put(1L, role);
        MockGenericDao roleDao = new MockGenericDao(objects);

        EntityReferenceEditor editor = new EntityReferenceEditor(new MockDaoService(roleDao), Role.class);
        editor.setAsText("org.otherobjects.cms.model.Role-1");
        assertEquals(role, editor.getValue());
    }

    public void testGetAsText()
    {
        Role role = new Role();
        role.setId(1L);
        role.setName("Test Role");

        EntityReferenceEditor editor = new EntityReferenceEditor(null, Role.class);
        editor.setValue(role);
        assertEquals("org.otherobjects.cms.model.Role-1", editor.getAsText());
    }

}
