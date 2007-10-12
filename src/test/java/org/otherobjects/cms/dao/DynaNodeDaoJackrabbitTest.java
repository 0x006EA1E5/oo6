//package org.otherobjects.cms.dao;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//import javax.jcr.Node;
//import javax.jcr.RepositoryException;
//import javax.jcr.Session;
//
//import org.apache.commons.beanutils.PropertyUtils;
//import org.otherobjects.cms.beans.BaseDynaNodeTest;
//import org.otherobjects.cms.jcr.OtherObjectsJackrabbitSessionFactory;
//import org.otherobjects.cms.model.DynaNode;
//import org.otherobjects.cms.model.SiteFolder;
//import org.otherobjects.cms.util.StringUtils;
//
//public class UniversalJcrDaoJackrabbitTest extends BaseDynaNodeTest
//{
//
//    protected OtherObjectsJackrabbitSessionFactory sessionFactory;
//
//    public void setJcrSessionFactory(OtherObjectsJackrabbitSessionFactory sessionFactory)
//    {
//        this.sessionFactory = sessionFactory;
//    }
//
//    private DynaNode createSampleArticle(String path, String name)
//    {
//        DynaNode a1 = universalJcrDao.create("Article");
//        a1.setPath(path);
//        a1.set("title", name);
//        return a1;
//    }
//
//    private DynaNode createSampleFolder(String path, String name)
//    {
//        DynaNode a1 = universalJcrDao.create(SiteFolder.class.getName());
//        a1.setPath(path);
//        a1.set("label", name);
//        return universalJcrDao.save(a1);
//    }
//
//    public void testGet()
//    {
//        DynaNode a1 = createSampleArticle("/site/", "A1 Title");
//        DynaNode a1s = universalJcrDao.save(a1);
//
//        DynaNode node = universalJcrDao.get(a1s.getId());
//        assertNotNull(node);
//        assertEquals(a1.getJcrPath(), node.getJcrPath());
//    }
//
//    public void testGetByPath()
//    {
//        // Resources
//        DynaNode node = universalJcrDao.getByPath("/site/welcome.html");
//        assertNotNull(node);
//        assertEquals("Welcome", node.get("title"));
//
//        // Folders
//        node = universalJcrDao.getByPath("/site/about/");
//        assertNotNull(node);
//        assertEquals("about", node.getCode());
//    }
//
//    public void testGetAllByPath()
//    {
//        // TODO Test if this includes folders
//
//        adminLogin();
//        // Testing that it is transaction safe
//        List<DynaNode> contents = universalJcrDao.getAllByPath("/site/about/");
//        assertEquals(1, contents.size());
//
//        DynaNode a1 = createSampleArticle("/site/about/", "A1 Title");
//        universalJcrDao.save(a1);
//        contents = universalJcrDao.getAllByPath("/site/about/");
//        assertEquals(2, contents.size());
//        logout();
//    }
//
//    public void testGetAllByType()
//    {
//        List<DynaNode> nodes = universalJcrDao.getAllByType("Article");
//        assertNotNull(nodes);
//        assertEquals(2, nodes.size());
//    }
//
//    public void testCreate()
//    {
//        //FIXME Implement test
//    }
//
//    public void testRemove()
//    {
//        DynaNode node = universalJcrDao.getByPath("/site/welcome.html");
//        assertNotNull(node);
//
//        universalJcrDao.remove(node.getId());
//        assertNull(universalJcrDao.get(node.getId()));
//    }
//
//    public void testExists()
//    {
//        DynaNode node = universalJcrDao.getByPath("/site/welcome.html");
//        assertTrue(universalJcrDao.exists(node.getId()));
//        // Corrupt UUID to create non-existing id
//        assertFalse(universalJcrDao.exists(node.getId().replaceAll("[0-9a-f]", "0")));
//        assertFalse(universalJcrDao.exists(null));
//    }
//
//    public void testExistsAtPath()
//    {
//        assertTrue(universalJcrDao.existsAtPath("/site/welcome.html"));
//        assertFalse(universalJcrDao.existsAtPath("/site/not-welcome.html"));
//        try
//        {
//            universalJcrDao.existsAtPath(null);
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
//        assertFalse(universalJcrDao.existsAtPath("/site/test/contact"));
//        universalJcrDao.moveItem(c.getId(), m.getId(), "append");
//        assertTrue(universalJcrDao.existsAtPath("/site/test/contact"));
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
//        universalJcrDao.moveItem(t3.getId(), t2.getId(), "above");
//        List<DynaNode> aContents = universalJcrDao.getAllByPath("/site/test");
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
//        universalJcrDao.moveItem(t1.getId(), t3.getId(), "below");
//        List<DynaNode> aContents = universalJcrDao.getAllByPath("/site/test");
//        assertEquals(3, aContents.size());
//        assertEquals(t1.getCode(), aContents.get(2).getCode());
//    }
//
//    public void testMove4()
//    {
//        DynaNode a = universalJcrDao.getByPath("/site/about");
//        createSampleFolder("/site/", "Test");
//        createSampleFolder("/site/test", "Test 1");
//        DynaNode t2 = createSampleFolder("/site/test", "Test 2");
//        createSampleFolder("/site/test", "Test 3");
//        
//        // 4. Move above different folder
//        universalJcrDao.moveItem(a.getId(), t2.getId(), "above");
//        List<DynaNode> contents = universalJcrDao.getAllByPath("/site/test");
//        assertEquals(4, contents.size());
//        assertEquals(a.getCode(), contents.get(1).getCode());
//    }
//
//    public void testMove5()
//    {
//        DynaNode a = universalJcrDao.getByPath("/site/about");
//        createSampleFolder("/site/", "Test");
//        createSampleFolder("/site/test", "Test 1");
//        DynaNode t2 = createSampleFolder("/site/test", "Test 2");
//        createSampleFolder("/site/test", "Test 3");
//        
//        // 5. Move below different folder
//        universalJcrDao.moveItem(a.getId(), t2.getId(), "below");
//        List<DynaNode> contents = universalJcrDao.getAllByPath("/site/test");
//        assertEquals(4, contents.size());
//        assertEquals(a.getCode(), contents.get(2).getCode());
//    }
//
//    @SuppressWarnings("unchecked")
//    public void testSave() throws RepositoryException
//    {
//        adminLogin();
//        //FIXME Re-enable this test
//        DynaNode r1 = createReference("R1");
//        List<DynaNode> referencesList = new ArrayList<DynaNode>();
//        referencesList.add(createReference("R3"));
//        referencesList.add(createReference("R4"));
//        referencesList.add(createReference("R5"));
//
//        DynaNode c1 = createComponent("C1");
//        // DynaNode c2 = createComponent("C2");
//        // Test nested 
//        // c1.set("component", c2);
//
//        List<DynaNode> componentsList = new ArrayList<DynaNode>();
//        componentsList.add(createComponent("C3"));
//        componentsList.add(createComponent("C4"));
//        componentsList.add(createComponent("C5"));
//
//        DynaNode n1 = universalJcrDao.create("org.otherobjects.Dyna.jcr.TestObject");
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
//        universalJcrDao.save(n1);
//
//        DynaNode ns2 = (DynaNode) universalJcrDao.getByPath("/site/news.html");
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
//        universalJcrDao.save(n1);
//        jcrMappingTemplate.save();
//
//        ns2 = (DynaNode) universalJcrDao.getByPath("/site/news.html");
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
//    public void testPublish() throws Exception
//    {
//        adminLogin();
//
//        DynaNode node = universalJcrDao.getByPath("/site/welcome.html");
//
//        assertNotNull(node);
//
//        String changedTitle = "changedTitle " + new Date().toString();
//
//        PropertyUtils.setSimpleProperty(node, "title", changedTitle);
//        node.setCode(StringUtils.generateUrlCode(node.getLabel()) + ".html");
//
//        universalJcrDao.save(node);
//
//        assertNotNull("Must not be null", universalJcrDao.getByPath(node.getJcrPath()));
//
//        long countBefore = universalJcrDao.getVersions(node).size();
//        System.out.println("There are " + countBefore + " in the repository before publishing");
//        universalJcrDao.publish(node, null);
//        long countAfter = universalJcrDao.getVersions(node).size();
//        System.out.println("There are " + countAfter + " in the repository after publishing");
//
//        assertTrue(countBefore < countAfter);
//
//        logout();
//
//        anoymousLogin();
//
//        DynaNode node1 = universalJcrDao.getByPath(node.getJcrPath());
//        System.out.println(node1.getJcrPath());
//        assertEquals(changedTitle, PropertyUtils.getSimpleProperty(node1, "title"));
//
//        logout();
//
//    }
//
//    public void testGetAllVersions() throws Exception
//    {
//        DynaNode node = universalJcrDao.getByPath("/site/welcome.html");
//        long versionCount = getVersionCount(node);
//
//        List<DynaNode> versions = universalJcrDao.getVersions(node);
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
//        DynaNode node = universalJcrDao.getByPath("/site/welcome.html");
//
//        String firstVersionTitle = "title1";
//
//        PropertyUtils.setSimpleProperty(node, "title", firstVersionTitle);
//        node.setCode(StringUtils.generateUrlCode(node.getLabel()) + ".html");
//
//        universalJcrDao.save(node);
//        universalJcrDao.publish(node, null);
//
//        int firstChangeNumber = node.getChangeNumber();
//
//        String secondVersionTitle = "title2";
//
//        PropertyUtils.setSimpleProperty(node, "title", secondVersionTitle);
//        node.setCode(StringUtils.generateUrlCode(node.getLabel()) + ".html");
//
//        universalJcrDao.save(node);
//        universalJcrDao.publish(node, null);
//
//        int secondChangeNumber = node.getChangeNumber();
//
//        assertEquals(firstVersionTitle, PropertyUtils.getSimpleProperty(universalJcrDao.getVersionByChangeNumber(node, firstChangeNumber), "title"));
//        assertEquals(secondVersionTitle, PropertyUtils.getSimpleProperty(universalJcrDao.getVersionByChangeNumber(node, secondChangeNumber), "title"));
//
//        logout();
//    }
//
//    //    public void testRestoreVersionByChangeNumber() throws Exception
//    //    {
//    //    	adminLogin();
//    //    	DynaNode node = universalJcrDao.getByPath("/site/about/index.html");
//    //    	
//    //    	String firstVersionTitle = "title1";
//    //    	
//    //    	PropertyUtils.setSimpleProperty(node, "title", firstVersionTitle);
//    //        node.setCode(StringUtils.generateUrlCode(node.getLabel()) + ".html");
//    //        
//    //        universalJcrDao.save(node);
//    //        universalJcrDao.publish(node);
//    //        
//    //        int firstChangeNumber = node.getChangeNumber();
//    //        System.out.println("cn: " + firstChangeNumber);
//    //        
//    //        String secondVersionTitle = "title2";
//    //    	
//    //    	PropertyUtils.setSimpleProperty(node, "title", secondVersionTitle);
//    //        node.setCode(StringUtils.generateUrlCode(node.getLabel()) + ".html");
//    //        
//    //        universalJcrDao.save(node);
//    //        universalJcrDao.publish(node);
//    //        System.out.println("cn: " + node.getChangeNumber());
//    //        
//    //        assertNotNull(universalJcrDao.getVersionByChangeNumber(node, firstChangeNumber));
//    //
//    //        DynaNode nodeRestored = universalJcrDao.restoreVersionByChangeNumber(node, firstChangeNumber, false);
//    //        
//    //        assertEquals(firstVersionTitle, PropertyUtils.getSimpleProperty(nodeRestored, "title"));
//    //        
//    //    	
//    //    	logout();
//    //    }
//
//    private long getVersionCount(DynaNode dynaNode)
//    {
//        Session editSession = null;
//        try
//        {
//            editSession = sessionFactory.getSession(OtherObjectsJackrabbitSessionFactory.EDIT_WORKSPACE_NAME);
//            Node node = editSession.getNodeByUUID(dynaNode.getId());
//
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
//}
