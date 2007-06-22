package org.otherobjects.cms.hibernate;

import org.otherobjects.cms.dao.RoleDao;
import org.otherobjects.cms.model.Role;
import org.otherobjects.cms.test.BaseDaoTestCase;

public class RoleDaoTest extends BaseDaoTestCase
{
    private RoleDao dao;

    public void setRoleDao(RoleDao dao)
    {
        this.dao = dao;
    }

    public void testGetRoleInvalid() throws Exception
    {
        Role role = dao.getRoleByName("badrolename");
        assertNull(role);
    }

    public void testGetRole() throws Exception
    {
        Role role = dao.getRoleByName("ROLE_USER");
        assertNotNull(role);
    }

    public void testUpdateRole() throws Exception
    {
        Role role = dao.getRoleByName("ROLE_USER");
        role.setDescription("test descr");
        dao.save(role);
        flush();

        role = dao.getRoleByName("ROLE_USER");
        assertEquals("test descr", role.getDescription());
    }

    public void testAddAndRemoveRole() throws Exception
    {
        Role role = new Role("testrole");
        role.setDescription("new role descr");
        dao.save(role);
        flush();

        role = dao.getRoleByName("testrole");
        assertNotNull(role.getDescription());

        dao.removeRole("testrole");
        flush();

        role = dao.getRoleByName("testrole");
        assertNull(role);
    }
}
