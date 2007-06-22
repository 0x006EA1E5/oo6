package org.otherobjects.cms.hibernate;

import org.otherobjects.cms.dao.RoleDao;
import org.otherobjects.cms.dao.UserDao;
import org.otherobjects.cms.model.Role;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.test.BaseDaoTestCase;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

public class UserDaoTest extends BaseDaoTestCase
{
    private UserDao dao = null;
    private RoleDao rdao = null;

    public void setUserDao(UserDao dao)
    {
        this.dao = dao;
    }

    public void setRoleDao(RoleDao rdao)
    {
        this.rdao = rdao;
    }

    public void testGetUserInvalid() throws Exception
    {
        try
        {
            dao.get(1000L);
            fail("'badusername' found in database, failing test...");
        }
        catch (DataAccessException d)
        {
            assertTrue(d != null);
        }
    }

    public void testGetUser() throws Exception
    {
        User user = dao.get(1L);

        assertNotNull(user);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.isEnabled());
    }

    public void testUpdateUser() throws Exception
    {
        User user = dao.get(1L);

        dao.saveUser(user);
        flush();

        user = dao.get(1L);

        // verify that violation occurs when adding new user with same username
        user.setId(null);

        endTransaction();

        try
        {
            dao.saveUser(user);
            flush();
            fail("saveUser didn't throw DataIntegrityViolationException");
        }
        catch (DataIntegrityViolationException e)
        {
            assertNotNull(e);
            log.debug("expected exception: " + e.getMessage());
        }
    }

    public void testAddUserRole() throws Exception
    {
        User user = dao.get(1L);
        assertEquals(1, user.getRoles().size());

        Role role = rdao.getRoleByName("ROLE_ADMIN");
        user.addRole(role);
        user = dao.saveUser(user);
        flush();

        user = dao.get(1L);
        assertEquals(2, user.getRoles().size());

        //add the same role twice - should result in no additional role
        user.addRole(role);
        dao.saveUser(user);
        flush();

        user = dao.get(1L);
        assertEquals("more than 2 roles", 2, user.getRoles().size());

        user.getRoles().remove(role);
        dao.saveUser(user);
        flush();

        user = dao.get(1L);
        assertEquals(1, user.getRoles().size());
    }

    public void testAddAndRemoveUser() throws Exception
    {
        User user = new User("testuser");
        user.setPassword("testpass");
        user.setFirstName("Test");
        user.setLastName("Last");
        user.setEmail("testuser@appfuse.org");

        Role role = rdao.getRoleByName("ROLE_USER");
        assertNotNull(role.getId());
        user.addRole(role);

        user = dao.saveUser(user);
        flush();

        assertNotNull(user.getId());
        user = dao.get(user.getId());
        assertEquals("testpass", user.getPassword());

        dao.remove(user.getId());
        flush();

        try
        {
            dao.get(user.getId());
            fail("getUser didn't throw DataAccessException");
        }
        catch (DataAccessException d)
        {
            assertNotNull(d);
        }
    }

    public void testUserExists() throws Exception
    {
        boolean b = dao.exists(1L);
        super.assertTrue(b);
    }

    public void testUserNotExists() throws Exception
    {
        boolean b = dao.exists(111L);
        super.assertFalse(b);
    }
}
