package org.otherobjects.cms.jcr;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.otherobjects.cms.binding.TestComponentObject;
import org.otherobjects.cms.binding.TestObject;
import org.otherobjects.cms.binding.TestReferenceObject;
import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.model.SiteFolder;
import org.otherobjects.framework.SingletonBeanLocator;
import org.otherobjects.framework.config.OtherObjectsConfigurator;

@SuppressWarnings("unchecked")
// FIXME Test for no results in all these mehods
// FIXME Test empty string handling in fields
public class GenericJcrDaoJackrabbitTest extends BaseJcrTestCase
{
    protected GenericJcrDao genericJcrDao;

    
    @Before
    public void setUp() throws Exception
    {
        if(System.getProperty(OtherObjectsConfigurator.ENVIRONMENT_SYSPROP_KEY) == null) {
            System.setProperty(OtherObjectsConfigurator.ENVIRONMENT_SYSPROP_KEY, "test");
        }
        // FIXME Need to clear down any data in db
        
        typeService.registerType(typeDefBuilder.getTypeDef(TestComponentObject.class));
        typeService.registerType(typeDefBuilder.getTypeDef(TestReferenceObject.class));
        typeService.registerType(typeDefBuilder.getTypeDef(TestObject.class));
        genericJcrDao = (GenericJcrDao) daoService.getDao(TestObject.class);
        SingletonBeanLocator.registerTestBean("typeService", typeService);
    }

    @After
    public void tearDown() throws Exception
    {
        SingletonBeanLocator.registerTestBean("typeSevice", null);
        
    }

    private TestObject createTestObject(String path, String code, String name)
    {
        TestObject a1 = new TestObject();
        a1.setTypeDef(typeService.getType(TestObject.class));
        a1.setPath(path);
        a1.setCode(code);
        a1.setName(name);
        TestComponentObject testComponent = new TestComponentObject("component1");
        a1.setTestComponent(testComponent);

        return (TestObject) genericJcrDao.save(a1);
    }

    private SiteFolder createSampleFolder(String path, String name)
    {
        SiteFolder folder = new SiteFolder();
        folder.setPath(path);
        folder.setPropertyValue("label", name);
        return (SiteFolder) genericJcrDao.save(folder);
    }

    @Test
    public void testSave() throws Exception
    {
        TestObject t1 = new TestObject();
        GenericJcrDao<TestObject> dao = (GenericJcrDao<TestObject>) daoService.getDao(TestObject.class);

        t1.setPath("/");
        t1.setName("Test Object");

        TestObject t1s = dao.save(t1);

        genericJcrDao.renderNodeInfo(t1s.getId());

        assertNotNull(t1s.getId());
        assertEquals("/test-object", t1s.getJcrPath());
        assertEquals(t1.getName(), t1s.getName());
    }

    @Test
    public void testSaveComplex() throws Exception
    {
        TestObject t1 = new TestObject();
        GenericJcrDao<TestObject> dao = (GenericJcrDao<TestObject>) daoService.getDao(TestObject.class);

        t1.setPath("/");
        t1.setName("Test Object");
        
        t1.setTestComponentsList(new ArrayList<TestComponentObject>());
        t1.getTestComponentsList().add(new TestComponentObject("list-component-1"));
        t1.getTestComponentsList().add(new TestComponentObject("list-component-2"));

        TestObject t1s = dao.save(t1);

        genericJcrDao.renderNodeInfo(t1s.getId());

        assertNotNull(t1s.getId());
        assertEquals("/test-object", t1s.getJcrPath());
        assertEquals(t1.getName(), t1s.getName());
        
//        DynaNode c1 = createComponent("C1");
//        DynaNode c2 = createComponent("C2");
//        // Test nested 
//        c1.set("component", c2);
//
//        List<DynaNode> componentsList = new ArrayList<DynaNode>();
//        componentsList.add(createComponent("C3"));
//        componentsList.add(createComponent("C4"));
//        componentsList.add(createComponent("C5"));
//
//        DynaNode n1 = genericJcrDao.create("org.otherobjects.Dyna.jcr.TestObject");
//        n1.setJcrPath("/site/news.html");
//        n1.set("testString", "News Story 1");
//        n1.set("testText", "Content of story");
//
//        Calendar date = Calendar.getInstance();
//        date.set(2000, 01, 02, 0, 0, 0);
//        Calendar time = Calendar.getInstance();
//        time.set(0, 0, 0, 03, 04, 05);
//        Calendar timestamp = Calendar.getInstance();
//        timestamp.set(2000, 01, 02, 03, 04, 05);
//        n1.set("testDate", date.getTime());
//        n1.set("testTime", time.getTime());
//        n1.set("testTimestamp", timestamp.getTime());
//
//        n1.set("testNumber", new Long(123456));
//        n1.set("testDecimal", new BigDecimal("19.95"));
//        n1.set("testBoolean", Boolean.TRUE);
//
//        n1.set("testComponent", c1);
//        n1.set("testReference", r1);
//
//        String[] s = {"S1", "S2", "S3"};
//        List<String> stringsList = new ArrayList<String>(Arrays.asList(s));
//        n1.set("testStringsList", stringsList);
//        n1.set("testComponentsList", componentsList);
//        n1.set("testReferencesList", referencesList);
//
//        genericJcrDao.save(n1);
//
//        DynaNode ns2 = (DynaNode) genericJcrDao.getByPath("/site/news.html");
//        assertEquals(n1.get("testString"), ns2.get("testString"));
//        assertEquals(n1.get("testText"), ns2.get("testText"));
//        assertEquals(n1.get("testDate"), ns2.get("testDate"));
//        assertEquals(n1.get("testTime"), ns2.get("testTime"));
//        assertEquals(n1.get("testTimestamp"), ns2.get("testTimestamp"));
//        assertEquals(n1.get("testNumber"), ns2.get("testNumber"));
//        assertEquals(n1.get("testDecimal"), ns2.get("testDecimal"));
//        assertEquals(n1.get("testBoolean"), ns2.get("testBoolean"));
//        assertNotNull(ns2.getId());
//
//        n1.set("testString", "News Story 1.1");
//        genericJcrDao.save(n1);
//        jcrMappingTemplate.save();
//
//        ns2 = (DynaNode) genericJcrDao.getByPath("/site/news.html");
//        assertEquals(n1.get("testString"), ns2.get("testString"));
//        assertEquals(c1.get("name"), ns2.get("testComponent.name"));
//        //        assertEquals(c2.get("name"), ns2.get("testComponent.component.name"));
//        assertEquals(r1.get("name"), ns2.get("testReference.name")); // Required reference
//
//        assertEquals(stringsList.size(), ((List) ns2.get("testStringsList")).size());
//        assertEquals(stringsList.get(1), ((List) ns2.get("testStringsList")).get(1));
//        //assertEquals(componentsList.size(), ((List) ns2.get("testComponentsList")).size());
//        //assertEquals(componentsList.get(1), ((List) ns2.get("testComponentsList")).get(1));
//        assertEquals(referencesList.size(), ((List) ns2.get("testReferencesList")).size());
//        //assertEquals(referencesList.get(1), ((List) ns2.get("testReferencesList")).get(1));
//        logout();
    }

    public void testGet()
    {
        TestObject welcome = createTestObject("/", "test.html", "Test Object");
        TestObject node = (TestObject) genericJcrDao.get(welcome.getId());
        assertNotNull(node);
        assertEquals(welcome.getJcrPath(), node.getJcrPath());
        assertEquals(welcome.getTestComponent().getName(), node.getTestComponent().getName());

        genericJcrDao.renderNodeInfo(node.getId());

    }

    public void testGetByPath()
    {
        createTestObject("/", "test.html", "Test Object");
        createSampleFolder("/", "Test");

        // Resources
        TestObject t1r = (TestObject) genericJcrDao.getByPath("/test.html");
        assertNotNull(t1r);
        assertEquals("Test Object", t1r.getName());

        // Folders
        SiteFolder f1r = (SiteFolder) genericJcrDao.getByPath("/test/");
        assertNotNull(f1r);
        assertEquals("Test", f1r.getLabel());
    }

    public void testRemove()
    {
        TestObject welcome = createTestObject("/", "test.html", "Test Object");
        TestObject node = (TestObject) genericJcrDao.get(welcome.getId());
        assertNotNull(node);

        genericJcrDao.remove(node.getId());
        assertNull(genericJcrDao.get(node.getId()));
    }

    public void testExists()
    {
        TestObject welcome = createTestObject("/", "test.html", "Test Object");
        assertTrue(genericJcrDao.exists(welcome.getId()));
        // Corrupt UUID to create non-existing id
        assertFalse(genericJcrDao.exists(welcome.getId().replaceAll("[0-9a-f]", "0")));
        try
        {
            genericJcrDao.exists(null);
            fail();
        }
        catch (RuntimeException e)
        {
            // TODO Explain why we ignore exception
        }
    }

    public void testExistsAtPath()
    {
        createTestObject("/", "test.html", "Test Object");
        assertTrue(genericJcrDao.existsAtPath("/test.html"));
        assertFalse(genericJcrDao.existsAtPath("/non-existent.html"));
        try
        {
            genericJcrDao.existsAtPath(null);
            fail();
        }
        catch (RuntimeException e)
        {
            // TODO Explain why we ignore exception
        }
    }

    //    public void testPublish() throws Exception
    //    {
    //
    //        /*
    //         * FIXME Test: Implement publish() test
    //         * Note: publish has some problem with nodes not created in the same transaction.
    //         * This test does not work yet. 
    //         */
    //        //        adminLogin();
    //        //
    //        //        TestObject welcome = createTestObject("/", "test2.html", "Test Object");
    //        //        
    //        //        assertTrue(genericJcrDao.existsAtPath("/test2.html"));
    //        //        
    //        //        assertNotNull(welcome);
    //        //
    //        //        // Edit object
    //        //        String changedName = "New name " + new Date().toString();
    //        //        welcome.setName(changedName);
    //        //        welcome = (TestObject) genericJcrDao.save(welcome);
    //        //
    //        //        
    //        //        long countBefore = genericJcrDao.getVersions(welcome).size();
    //        //        System.out.println("There are " + countBefore + " in the repository before publishing");
    //        //        
    //        //        genericJcrDao.publish(welcome, null);
    //        //        long countAfter = genericJcrDao.getVersions(welcome).size();
    //        //        System.out.println("There are " + countAfter + " in the repository after publishing");
    //        //
    //        //        long countBefore=0;
    //        //        assertEquals(countBefore, countAfter - 1);
    //        //
    //        //        logout();
    //        //
    //        //        anoymousLogin();
    //        //
    //        //        TestObject node1 = (TestObject) genericJcrDao.getByPath(welcome.getJcrPath());
    //        //        String changedName="Test Object";
    //        //        assertEquals(changedName, node1.getName());
    //        //
    //        //        logout();
    //    }

    //    public void testGetAllVersions() throws Exception
    //    {
    //        DynaNode node = genericJcrDao.getByPath("/site/test.html");
    //        long versionCount = getVersionCount(node);
    //
    //        List<DynaNode> versions = genericJcrDao.getVersions(node);
    //
    //        int objectVersionCount = versions.size();
    //        assertTrue((int) versionCount == ++objectVersionCount); // object version count should be one less as we ignore the root version
    //
    //        for (DynaNode dn : versions)
    //        {
    //            System.out.println(PropertyUtils.getSimpleProperty(dn, "title"));
    //        }
    //    }
    //
    //    public void testGetVersionByChangeNumber() throws Exception
    //    {
    //        adminLogin();
    //        DynaNode node = genericJcrDao.getByPath("/site/test.html");
    //
    //        String firstVersionTitle = "title1";
    //
    //        PropertyUtils.setSimpleProperty(node, "title", firstVersionTitle);
    //        node.setCode(StringUtils.generateUrlCode(node.getLabel()) + ".html");
    //
    //        genericJcrDao.save(node);
    //        genericJcrDao.publish(node, null);
    //
    //        int firstChangeNumber = node.getChangeNumber();
    //
    //        String secondVersionTitle = "title2";
    //
    //        PropertyUtils.setSimpleProperty(node, "title", secondVersionTitle);
    //        node.setCode(StringUtils.generateUrlCode(node.getLabel()) + ".html");
    //
    //        genericJcrDao.save(node);
    //        genericJcrDao.publish(node, null);
    //
    //        int secondChangeNumber = node.getChangeNumber();
    //
    //        assertEquals(firstVersionTitle, PropertyUtils.getSimpleProperty(genericJcrDao.getVersionByChangeNumber(node, firstChangeNumber), "title"));
    //        assertEquals(secondVersionTitle, PropertyUtils.getSimpleProperty(genericJcrDao.getVersionByChangeNumber(node, secondChangeNumber), "title"));
    //
    //        logout();
    //    }
    //
    //    public void testRestoreVersionByChangeNumber() throws Exception
    //    {
    //        adminLogin();
    //        DynaNode node = genericJcrDao.getByPath("/site/about/index.html");
    //
    //        String firstVersionTitle = "title1";
    //
    //        PropertyUtils.setSimpleProperty(node, "title", firstVersionTitle);
    //        node.setCode(StringUtils.generateUrlCode(node.getLabel()) + ".html");
    //
    //        genericJcrDao.save(node);
    //        genericJcrDao.publish(node, null);
    //
    //        int firstChangeNumber = node.getChangeNumber();
    //        System.out.println("cn: " + firstChangeNumber);
    //
    //        String secondVersionTitle = "title2";
    //
    //        PropertyUtils.setSimpleProperty(node, "title", secondVersionTitle);
    //        node.setCode(StringUtils.generateUrlCode(node.getLabel()) + ".html");
    //
    //        genericJcrDao.save(node);
    //        genericJcrDao.publish(node, null);
    //        System.out.println("cn: " + node.getChangeNumber());
    //
    //        assertNotNull(genericJcrDao.getVersionByChangeNumber(node, firstChangeNumber));
    //
    //        DynaNode nodeRestored = genericJcrDao.restoreVersionByChangeNumber(node, firstChangeNumber);
    //
    //        assertEquals(firstVersionTitle, PropertyUtils.getSimpleProperty(nodeRestored, "title"));
    //
    //        logout();
    //    }

    //    private long getVersionCount(DynaNode dynaNode)
    //    {
    //        Session editSession = null;
    //        try
    //        {
    //            editSession = sessionFactory.getSession(OtherObjectsJackrabbitSessionFactory.EDIT_WORKSPACE_NAME);
    //            Node node = editSession.getNodeByUUID(dynaNode.getId());
    //            return node.getVersionHistory().getAllVersions().getSize();
    //        }
    //        catch (RepositoryException e)
    //        {
    //            //noop
    //        }
    //        finally
    //        {
    //            if (editSession != null)
    //                editSession.logout();
    //        }
    //        return 0;
    //    }

    //    public void testSave() throws RepositoryException
    //    {
    //        adminLogin();
    //        
    //        DynaNode r1 = createReference("R1");
    //        List<DynaNode> referencesList = new ArrayList<DynaNode>();
    //        referencesList.add(createReference("R3"));
    //        referencesList.add(createReference("R4"));
    //        referencesList.add(createReference("R5"));
    //
    //        DynaNode c1 = createComponent("C1");
    //         DynaNode c2 = createComponent("C2");
    //        // Test nested 
    //         c1.set("component", c2);
    //
    //        List<DynaNode> componentsList = new ArrayList<DynaNode>();
    //        componentsList.add(createComponent("C3"));
    //        componentsList.add(createComponent("C4"));
    //        componentsList.add(createComponent("C5"));
    //
    //        DynaNode n1 = genericJcrDao.create("org.otherobjects.Dyna.jcr.TestObject");
    //        n1.setJcrPath("/site/news.html");
    //        n1.set("testString", "News Story 1");
    //        n1.set("testText", "Content of story");
    //
    //        Calendar date = Calendar.getInstance();
    //        date.set(2000, 01, 02, 0, 0, 0);
    //        Calendar time = Calendar.getInstance();
    //        time.set(0, 0, 0, 03, 04, 05);
    //        Calendar timestamp = Calendar.getInstance();
    //        timestamp.set(2000, 01, 02, 03, 04, 05);
    //        n1.set("testDate", date.getTime());
    //        n1.set("testTime", time.getTime());
    //        n1.set("testTimestamp", timestamp.getTime());
    //
    //        n1.set("testNumber", new Long(123456));
    //        n1.set("testDecimal", new BigDecimal("19.95"));
    //        n1.set("testBoolean", Boolean.TRUE);
    //
    //        n1.set("testComponent", c1);
    //        n1.set("testReference", r1);
    //
    //        String[] s = {"S1", "S2", "S3"};
    //        List<String> stringsList = new ArrayList<String>(Arrays.asList(s));
    //        n1.set("testStringsList", stringsList);
    //        n1.set("testComponentsList", componentsList);
    //        n1.set("testReferencesList", referencesList);
    //
    //        genericJcrDao.save(n1);
    //
    //        DynaNode ns2 = (DynaNode) genericJcrDao.getByPath("/site/news.html");
    //        assertEquals(n1.get("testString"), ns2.get("testString"));
    //        assertEquals(n1.get("testText"), ns2.get("testText"));
    //        assertEquals(n1.get("testDate"), ns2.get("testDate"));
    //        assertEquals(n1.get("testTime"), ns2.get("testTime"));
    //        assertEquals(n1.get("testTimestamp"), ns2.get("testTimestamp"));
    //        assertEquals(n1.get("testNumber"), ns2.get("testNumber"));
    //        assertEquals(n1.get("testDecimal"), ns2.get("testDecimal"));
    //        assertEquals(n1.get("testBoolean"), ns2.get("testBoolean"));
    //        assertNotNull(ns2.getId());
    //
    //        n1.set("testString", "News Story 1.1");
    //        genericJcrDao.save(n1);
    //        jcrMappingTemplate.save();
    //
    //        ns2 = (DynaNode) genericJcrDao.getByPath("/site/news.html");
    //        assertEquals(n1.get("testString"), ns2.get("testString"));
    //        assertEquals(c1.get("name"), ns2.get("testComponent.name"));
    //        //        assertEquals(c2.get("name"), ns2.get("testComponent.component.name"));
    //        assertEquals(r1.get("name"), ns2.get("testReference.name")); // Required reference
    //
    //        assertEquals(stringsList.size(), ((List) ns2.get("testStringsList")).size());
    //        assertEquals(stringsList.get(1), ((List) ns2.get("testStringsList")).get(1));
    //        //assertEquals(componentsList.size(), ((List) ns2.get("testComponentsList")).size());
    //        //assertEquals(componentsList.get(1), ((List) ns2.get("testComponentsList")).get(1));
    //        assertEquals(referencesList.size(), ((List) ns2.get("testReferencesList")).size());
    //        //assertEquals(referencesList.get(1), ((List) ns2.get("testReferencesList")).get(1));
    //        logout();
    //    }
    //
    //    public void testGetAllByPath()
    //    {
    //        // TODO Test if this includes folders
    //
    //        adminLogin();
    //        // Testing that it is transaction safe
    //        List<DynaNode> contents = genericJcrDao.getAllByPath("/site/about/");
    //        assertEquals(1, contents.size());
    //
    //        DynaNode a1 = createSampleArticle("/site/about/", "A1 Title");
    //        genericJcrDao.save(a1);
    //        contents = genericJcrDao.getAllByPath("/site/about/");
    //        assertEquals(2, contents.size());
    //        logout();
    //    }
    //
    //    public void testGetAllByType()
    //    {
    //        List<DynaNode> nodes = genericJcrDao.getAllByType("Article");
    //        assertNotNull(nodes);
    //        assertEquals(2, nodes.size());
    //    }
    //
    //    public void testRemove()
    //    {
    //        DynaNode node = genericJcrDao.getByPath("/site/test.html");
    //        assertNotNull(node);
    //
    //        genericJcrDao.remove(node.getId());
    //        assertNull(genericJcrDao.get(node.getId()));
    //    }
    //
    //    public void testExists()
    //    {
    //        DynaNode node = genericJcrDao.getByPath("/site/test.html");
    //        assertTrue(genericJcrDao.exists(node.getId()));
    //        // Corrupt UUID to create non-existing id
    //        assertFalse(genericJcrDao.exists(node.getId().replaceAll("[0-9a-f]", "0")));
    //        assertFalse(genericJcrDao.exists(null));
    //    }
    //
    //    public void testExistsAtPath()
    //    {
    //        assertTrue(genericJcrDao.existsAtPath("/site/test.html"));
    //        assertFalse(genericJcrDao.existsAtPath("/site/not-test.html"));
    //        try
    //        {
    //            genericJcrDao.existsAtPath(null);
    //            fail();
    //        }
    //        catch (RuntimeException e)
    //        {
    //        }
    //    }
    //
    //    public void testRename()
    //    {
    //        // TODO Implement me
    //    }
    //
    //    public void testMove1()
    //    {
    //        DynaNode c = createSampleFolder("/site/", "contact");
    //        DynaNode m = createSampleFolder("/site/", "test");
    //
    //        // 1. Append to folder
    //        assertFalse(genericJcrDao.existsAtPath("/site/test/contact"));
    //        genericJcrDao.reorder(c, m, "append");
    //        assertTrue(genericJcrDao.existsAtPath("/site/test/contact"));
    //    }
    //
    //    public void testMove2()
    //    {
    //        createSampleFolder("/site/", "Test");
    //        createSampleFolder("/site/test", "Test 1");
    //        DynaNode t2 = createSampleFolder("/site/test", "Test 2");
    //        DynaNode t3 = createSampleFolder("/site/test", "Test 3");
    //
    //        // 2. Move above in same folder
    //        genericJcrDao.reorder(t3, t2, "above");
    //        List<DynaNode> aContents = genericJcrDao.getAllByPath("/site/test");
    //        assertEquals(3, aContents.size());
    //        assertEquals(t3.getCode(), aContents.get(1).getCode());
    //    }
    //
    //    public void testMove3()
    //    {
    //        createSampleFolder("/site/", "Test");
    //        DynaNode t1 = createSampleFolder("/site/test", "Test 1");
    //        createSampleFolder("/site/test", "Test 2");
    //        DynaNode t3 = createSampleFolder("/site/test", "Test 3");
    //
    //        // 3. Move below in same folder
    //        genericJcrDao.reorder(t1, t3, "below");
    //        List<DynaNode> aContents = genericJcrDao.getAllByPath("/site/test");
    //        assertEquals(3, aContents.size());
    //        assertEquals(t1.getCode(), aContents.get(2).getCode());
    //    }
    //
    //    public void testMove4()
    //    {
    //        DynaNode a = genericJcrDao.getByPath("/site/about");
    //        createSampleFolder("/site/", "Test");
    //        createSampleFolder("/site/test", "Test 1");
    //        DynaNode t2 = createSampleFolder("/site/test", "Test 2");
    //        createSampleFolder("/site/test", "Test 3");
    //
    //        // 4. Move above different folder
    //        genericJcrDao.reorder(a, t2, "above");
    //        List<DynaNode> contents = genericJcrDao.getAllByPath("/site/test");
    //        assertEquals(4, contents.size());
    //        assertEquals(a.getCode(), contents.get(1).getCode());
    //    }
    //
    //    public void testMove5()
    //    {
    //        DynaNode a = genericJcrDao.getByPath("/site/about");
    //        createSampleFolder("/site/", "Test");
    //        createSampleFolder("/site/test", "Test 1");
    //        DynaNode t2 = createSampleFolder("/site/test", "Test 2");
    //        createSampleFolder("/site/test", "Test 3");
    //
    //        // 5. Move below different folder
    //        genericJcrDao.reorder(a, t2, "below");
    //        List<DynaNode> contents = genericJcrDao.getAllByPath("/site/test");
    //        assertEquals(4, contents.size());
    //        assertEquals(a.getCode(), contents.get(2).getCode());
    //    }
    //
    //   
}
