//package org.otherobjects.cms.hibernate;
//
//import org.otherobjects.cms.dao.RoleDao;
//import org.otherobjects.cms.dao.UserDao;
//import org.otherobjects.cms.model.Role;
//import org.otherobjects.cms.model.User;
//import org.otherobjects.cms.test.BaseDaoTestCase;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.test.context.ContextConfiguration;
//
//@ContextConfiguration(locations={"classpath:/org/otherobjects/cms/bootstrap/shared-test-context.xml"})
//public class UserDaoTest extends BaseDaoTestCase
//{
//    @Autowired
//    private UserDao dao;
//    
//    @Autowired
//    private RoleDao rdao;
//
//    @Override
//    protected void setUp() throws Exception
//    {
//        super.setUp();
//        loadSeedData("org/otherobjects/cms/hibernate/users-seed.xml");
//    }
//
//    public void testGetUserInvalid() throws Exception
//    {
//        try
//        {
//            this.dao.get(1000L);
//            fail("'badusername' found in database, failing test...");
//        }
//        catch (DataAccessException d)
//        {
//            assertTrue(d != null);
//        }
//    }
//
//    public void testGetUser() throws Exception
//    {
//        User user = this.dao.get(1L);
//
//        assertNotNull(user);
//        assertEquals(1, user.getRoles().size());
//        assertTrue(user.isEnabled());
//    }
//
//    public void testUpdateUser() throws Exception
//    {
//        User user = this.dao.get(1L);
//
//        this.dao.saveUser(user);
//        flush();
//
//        user = this.dao.get(1L);
//
//        // verify that violation occurs when adding new user with same username
//        User u = new User();
//        u.setUsername("admin");
//
//        try
//        {
//            this.dao.saveUser(u);
//            flush();
//            fail("saveUser didn't throw DataIntegrityViolationException");
//        }
//        catch (DataIntegrityViolationException e)
//        {
//            assertNotNull(e);
//            this.logger.debug("expected exception: " + e.getMessage());
//        }
//    }
//
//    public void testAddUserRole() throws Exception
//    {
//        User user = this.dao.get(1L);
//        assertEquals(1, user.getRoles().size());
//
//        Role role = this.rdao.getRoleByName("ROLE_ADMIN");
//        user.addRole(role);
//        user = this.dao.saveUser(user);
//        flush();
//
//        user = this.dao.get(1L);
//        assertEquals(2, user.getRoles().size());
//
//        // FIXME This is disabled since user stores roles in a list atm
//        //add the same role twice - should result in no additional role
//        //        user.addRole(role);
//        //        dao.saveUser(user);
//        //        flush();
//        //
//        //        user = dao.get(1L);
//        //        assertEquals("more than 2 roles", 2, user.getRoles().size());
//
//        user.getRoles().remove(role);
//        this.dao.saveUser(user);
//        flush();
//
//        user = this.dao.get(1L);
//        assertEquals(1, user.getRoles().size());
//    }
//
//    public void testAddAndRemoveUser() throws Exception
//    {
//        User user = new User("testuser");
//        user.setPassword("testpass");
//        user.setFirstName("Test");
//        user.setLastName("Last");
//        user.setEmail("testuser@appfuse.org");
//
//        Role role = this.rdao.getRoleByName("ROLE_USER");
//        assertNotNull(role.getId());
//        user.addRole(role);
//
//        user = this.dao.saveUser(user);
//        flush();
//
//        assertNotNull(user.getId());
//        user = this.dao.get(user.getId());
//        assertEquals("testpass", user.getPassword());
//
//        this.dao.remove(user.getId());
//        flush();
//
//        try
//        {
//            this.dao.get(user.getId());
//            fail("getUser didn't throw DataAccessException");
//        }
//        catch (DataAccessException d)
//        {
//            assertNotNull(d);
//        }
//    }
//
//    public void testUserExists() throws Exception
//    {
//        boolean b = this.dao.exists(1L);
//        super.assertTrue(b);
//    }
//
//    public void testUserNotExists() throws Exception
//    {
//        boolean b = this.dao.exists(111L);
//        super.assertFalse(b);
//    }
//}
