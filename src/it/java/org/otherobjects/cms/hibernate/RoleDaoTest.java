//package org.otherobjects.cms.hibernate;
//
//import org.otherobjects.cms.dao.RoleDao;
//import org.otherobjects.cms.model.Role;
//import org.otherobjects.cms.test.BaseDaoTestCase;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//
//@ContextConfiguration(locations={"classpath:/org/otherobjects/cms/bootstrap/shared-test-context.xml"})
//public class RoleDaoTest extends BaseDaoTestCase
//{
//    @Autowired
//    private RoleDao dao;
//
//    public void setRoleDao(RoleDao dao)
//    {
//        this.dao = dao;
//    }
//    
//    @Override
//    protected void setUp() throws Exception
//    {
//        super.setUp();
//        loadSeedData("org/otherobjects/cms/hibernate/roles-seed.xml");
//    }
//
//    public void testGetRoleInvalid() throws Exception
//    {
//        Role role = dao.getRoleByName("badrolename");
//        assertNull(role);
//    }
//
//    public void testGetRole() throws Exception
//    {
//        Role role = dao.getRoleByName("ROLE_USER");
//        assertNotNull(role);
//    }
//
//    public void testUpdateRole() throws Exception
//    {
//        Role role = dao.getRoleByName("ROLE_USER");
//        role.setDescription("test descr");
//        dao.save(role);
//        flush();
//
//        role = dao.getRoleByName("ROLE_USER");
//        assertEquals("test descr", role.getDescription());
//    }
//
//    public void testAddAndRemoveRole() throws Exception
//    {
//        Role role = new Role("testrole","test description");
//        dao.save(role);
//        flush();
//
//        role = dao.getRoleByName("testrole");
//        assertNotNull(role.getDescription());
//
//        dao.removeRole("testrole");
//        flush();
//
//        role = dao.getRoleByName("testrole");
//        assertNull(role);
//    }
//}
