package org.otherobjects.cms.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jcr.RepositoryException;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.MockAuthenticationManager;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationProvider;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationToken;
import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.beans.BaseDynaNodeTest;
import org.otherobjects.cms.jcr.OtherObjectsJackrabbitSessionFactory;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.util.StringUtils;

public class DynaNodeDaoJackrabbitTest extends BaseDynaNodeTest
{
	
	protected OtherObjectsJackrabbitSessionFactory sessionFactory;
	
	public void setSessionFactory(
			OtherObjectsJackrabbitSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void testGet()
    {
        DynaNode node = dynaNodeDao.get("756ec0e7-300c-4d41-b9b6-6a2ccf823675");
        assertNotNull(node);
        assertEquals("/site/about", node.getJcrPath());
    }

    public void testGetByPath()
    {
        // Resources
        DynaNode node = dynaNodeDao.getByPath("/site/about/index.html");
        assertNotNull(node);
        assertEquals("About us", node.get("title"));

        // Folders
        node = dynaNodeDao.getByPath("/site/about/");
        assertNotNull(node);
        assertEquals("about", node.getCode());
    }

    public void testGetAllByPath()
    {
        // Testing that it is transaction safe
        List<DynaNode> contents = dynaNodeDao.getAllByPath("/site/test");
        assertEquals(3, contents.size());

        DynaNode folder = dynaNodeDao.create("Folder");
        folder.setPath("/site/test/");
        folder.setLabel("Test 4");
        folder.setCode("test4");
        dynaNodeDao.save(folder);
        contents = dynaNodeDao.getAllByPath("/site/test");
        assertEquals(4, contents.size());
    }

    public void testCreate()
    {
        //FIXME Implement test
    }

    public void testRemove()
    {
        DynaNode node = dynaNodeDao.getByPath("/site/about/index.html");
        assertNotNull(node);

        dynaNodeDao.remove(node.getId());
        assertNull(dynaNodeDao.get(node.getId()));
    }

    public void testExistsAtPath()
    {
        assertTrue(dynaNodeDao.existsAtPath("/site/about/index.html"));
        assertFalse(dynaNodeDao.existsAtPath("/site/about/not-about-us.html"));
        try
        {
            dynaNodeDao.existsAtPath(null);
            fail();
        }
        catch (RuntimeException e)
        {

        }
    }

    public void testRename()
    {

    }

    public void testMove1()
    {
        DynaNode c = dynaNodeDao.getByPath("/site/contact");
        DynaNode m = dynaNodeDao.getByPath("/site/test");

        // 1. Append to folder
        assertFalse(dynaNodeDao.existsAtPath("/site/test/contact"));
        dynaNodeDao.moveItem(c.getId(), m.getId(), "append");
        assertTrue(dynaNodeDao.existsAtPath("/site/test/contact"));
    }

    public void testMove2()
    {
        DynaNode t2 = dynaNodeDao.getByPath("/site/test/test2");
        DynaNode t3 = dynaNodeDao.getByPath("/site/test/test3");

        // 2. Move above in same folder
        dynaNodeDao.moveItem(t3.getId(), t2.getId(), "above");
        List<DynaNode> aContents = dynaNodeDao.getAllByPath("/site/test");
        assertEquals(3, aContents.size());
        assertEquals(t3.getCode(), aContents.get(1).getCode());
    }

    public void testMove3()
    {
        DynaNode t1 = dynaNodeDao.getByPath("/site/test/test1");
        DynaNode t3 = dynaNodeDao.getByPath("/site/test/test3");

        // 3. Move below in same folder
        dynaNodeDao.moveItem(t1.getId(), t3.getId(), "below");
        List<DynaNode> aContents = dynaNodeDao.getAllByPath("/site/test");
        assertEquals(3, aContents.size());
        assertEquals(t1.getCode(), aContents.get(2).getCode());
    }

    public void testMove4()
    {
        DynaNode c = dynaNodeDao.getByPath("/site/contact");
        DynaNode t2 = dynaNodeDao.getByPath("/site/test/test2");

        // 4. Move above different folder
        dynaNodeDao.moveItem(c.getId(), t2.getId(), "above");
        List<DynaNode> contents = dynaNodeDao.getAllByPath("/site/test");
        assertEquals(4, contents.size());
        assertEquals(c.getCode(), contents.get(1).getCode());
    }

    public void testMove5()
    {
        DynaNode c = dynaNodeDao.getByPath("/site/contact");
        DynaNode t2 = dynaNodeDao.getByPath("/site/test/test2");

        // 5. Move below different folder
        dynaNodeDao.moveItem(c.getId(), t2.getId(), "below");
        List<DynaNode> contents = dynaNodeDao.getAllByPath("/site/test");
        assertEquals(4, contents.size());
        assertEquals(c.getCode(), contents.get(2).getCode());
    }

    public void testExists()
    {
        DynaNode node = dynaNodeDao.getByPath("/site/about/index.html");
        assertTrue(dynaNodeDao.exists(node.getId()));
        assertFalse(dynaNodeDao.exists(node.getId().replaceAll("[0-9a-f]", "0")));
        assertFalse(dynaNodeDao.exists(null));
    }

    @SuppressWarnings("unchecked")
    public void testSave() throws RepositoryException
    {
        //FIXME Re-enable this test
        DynaNode r1 = createReference("R1");
        List<DynaNode> referencesList = new ArrayList<DynaNode>();
        referencesList.add(createReference("R3"));
        referencesList.add(createReference("R4"));
        referencesList.add(createReference("R5"));

        DynaNode c1 = createComponent("C1");
        // DynaNode c2 = createComponent("C2");
        // Test nested 
        // c1.set("component", c2);

        List<DynaNode> componentsList = new ArrayList<DynaNode>();
        componentsList.add(createComponent("C3"));
        componentsList.add(createComponent("C4"));
        componentsList.add(createComponent("C5"));

        DynaNode n1 = dynaNodeDao.create("org.otherobjects.Dyna.jcr.TestObject");
        n1.setJcrPath("/site/news.html");
        n1.set("testString", "News Story 1");
        n1.set("testText", "Content of story");

        Calendar date = Calendar.getInstance();
        date.set(2000, 01, 02, 0, 0, 0);
        Calendar time = Calendar.getInstance();
        time.set(0, 0, 0, 03, 04, 05);
        Calendar timestamp = Calendar.getInstance();
        timestamp.set(2000, 01, 02, 03, 04, 05);
        n1.set("testDate", date.getTime());
        n1.set("testTime", time.getTime());
        n1.set("testTimestamp", timestamp.getTime());

        n1.set("testNumber", new Long(123456));
        n1.set("testDecimal", new BigDecimal("19.95"));
        n1.set("testBoolean", Boolean.TRUE);

        n1.set("testComponent", c1);
        n1.set("testReference", r1);

        String[] s = {"S1", "S2", "S3"};
        List<String> stringsList = new ArrayList<String>(Arrays.asList(s));
        n1.set("testStringsList", stringsList);
        n1.set("testComponentsList", componentsList);
        n1.set("testReferencesList", referencesList);

        dynaNodeDao.save(n1);

        DynaNode ns2 = (DynaNode) dynaNodeDao.getByPath("/site/news.html");
        assertEquals(n1.get("testString"), ns2.get("testString"));
        assertEquals(n1.get("testText"), ns2.get("testText"));
        assertEquals(n1.get("testDate"), ns2.get("testDate"));
        assertEquals(n1.get("testTime"), ns2.get("testTime"));
        assertEquals(n1.get("testTimestamp"), ns2.get("testTimestamp"));
        assertEquals(n1.get("testNumber"), ns2.get("testNumber"));
        assertEquals(n1.get("testDecimal"), ns2.get("testDecimal"));
        assertEquals(n1.get("testBoolean"), ns2.get("testBoolean"));
        assertNotNull(ns2.getId());

        n1.set("testString", "News Story 1.1");
        dynaNodeDao.save(n1);
        jcrMappingTemplate.save();

        ns2 = (DynaNode) dynaNodeDao.getByPath("/site/news.html");
        assertEquals(n1.get("testString"), ns2.get("testString"));
        assertEquals(c1.get("name"), ns2.get("testComponent.name"));
        //        assertEquals(c2.get("name"), ns2.get("testComponent.component.name"));
        assertEquals(r1.get("name"), ns2.get("testReference.name")); // Required reference

        assertEquals(stringsList.size(), ((List) ns2.get("testStringsList")).size());
        assertEquals(stringsList.get(1), ((List) ns2.get("testStringsList")).get(1));
        //assertEquals(componentsList.size(), ((List) ns2.get("testComponentsList")).size());
        //assertEquals(componentsList.get(1), ((List) ns2.get("testComponentsList")).get(1));
        assertEquals(referencesList.size(), ((List) ns2.get("testReferencesList")).size());
        //assertEquals(referencesList.get(1), ((List) ns2.get("testReferencesList")).get(1));
    }

    public void testGetAllByType()
    {
        List<DynaNode> nodes = dynaNodeDao.getAllByType("Article");
        assertNotNull(nodes);
        assertEquals(3, nodes.size());
    }
    
    public void testPublish() throws Exception
    {
    	// pretend an editor session
    	// fake admin
    	User admin = new User("admin");
    	admin.setId(new Long(1));
    	
    	SecurityContextHolder.getContext().setAuthentication(new MockAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(admin, "admin", new GrantedAuthority[]{new GrantedAuthorityImpl(OtherObjectsJackrabbitSessionFactory.EDITOR_ROLE_NAME)})
		));
    	
    	DynaNode node = dynaNodeDao.getByPath("/site/about/index.html");

        assertNotNull(node);
        
        String changedTitle = "changedTitle " + new Date().toString();
        
        PropertyUtils.setSimpleProperty(node, "title", changedTitle);
        node.setCode(StringUtils.generateUrlCode(node.getLabel()) + ".html");
        
        dynaNodeDao.save(node);
        
        assertNotNull( "Must not be null", dynaNodeDao.getByPath(node.getJcrPath()));

        
        dynaNodeDao.publish(node);

        
        SecurityContextHolder.clearContext(); //logout
        
        // pretend anonymous user
        AnonymousAuthenticationProvider anonymousAuthenticationProvider = new AnonymousAuthenticationProvider();
        anonymousAuthenticationProvider.setKey("testkey");
        AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("testkey", "anonymous", new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_ANONYMOUS")});
        SecurityContextHolder.getContext().setAuthentication(anonymousAuthenticationProvider.authenticate(anonymousAuthenticationToken));

        
        DynaNode node1 = dynaNodeDao.getByPath(node.getJcrPath());
        System.out.println(node1.getJcrPath());
        assertEquals(changedTitle, PropertyUtils.getSimpleProperty(node1, "title"));
        
        SecurityContextHolder.clearContext();
        
    }
}
