package org.otherobjects.cms.jcr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.jcr.RepositoryException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jackrabbit.ocm.persistence.impl.PersistenceManagerImpl;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;

public class PersistenceManagerImplTest extends JcrTestBase
{
    private TypeService types;

    public PersistenceManagerImplTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        return new RepositoryLifecycleTestSetup(new TestSuite(PersistenceManagerImplTest.class));
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        cleanUpRepository();
        setupTypesService();
    }

    private void setupTypesService()
    {
        types = TypeService.getInstance();
        types.reset();

        TypeDef td = new TypeDef("org.otherobjects.cms.jcr.TestObject");
        td.addProperty(new PropertyDef("testString", "string", null, null));
        td.addProperty(new PropertyDef("testText", "text", null, null));
        td.addProperty(new PropertyDef("testDate", "date", null, null));
        td.addProperty(new PropertyDef("testTime", "time", null, null));
        td.addProperty(new PropertyDef("testTimestamp", "timestamp", null, null));
        td.addProperty(new PropertyDef("testNumber", "number", null, null));
        td.addProperty(new PropertyDef("testDecimal", "decimal", null, null));
        td.addProperty(new PropertyDef("testBoolean", "boolean", null, null));
        td.addProperty(new PropertyDef("testReference", "reference", "org.otherobjects.cms.jcr.TestReferenceObject", null));
        td.addProperty(new PropertyDef("testComponent", "component", "org.otherobjects.cms.jcr.TestComponentObject", null));
        td.addProperty(new PropertyDef("testStringsList", "string", null, "list"));
        td.addProperty(new PropertyDef("testComponentsList", "component", null, "list"));
        td.addProperty(new PropertyDef("testReferencesList", "reference", null, "list"));
        types.registerType(td);

        TypeDef td2 = new TypeDef("org.otherobjects.cms.jcr.TestReferenceObject");
        td2.addProperty(new PropertyDef("name", "string", null, null));
        types.registerType(td2);

        TypeDef td3 = new TypeDef("org.otherobjects.cms.jcr.TestComponentObject");
        td3.addProperty(new PropertyDef("name", "string", null, null));
        td3.addProperty(new PropertyDef("component", "component", "org.otherobjects.cms.jcr.TestComponentObject", null));
        types.registerType(td3);

    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    @SuppressWarnings("unchecked")
    public void testSaveCmsNode() throws RepositoryException
    {
        PersistenceManagerImpl persistenceManager = (PersistenceManagerImpl) getPersistenceManager(types);

//        CmsNode r1 = new TestReferenceObject();
//        CmsNode c1 = new TestComponentObject();
//        CmsNode n1 = new TestObject();

        CmsNode r1 = createReference("R1");
        List<CmsNode> referencesList = new ArrayList<CmsNode>();
        referencesList.add(createReference("R3"));
        referencesList.add(createReference("R4"));
        referencesList.add(createReference("R5"));

        CmsNode c1 = createComponent("C1");
        CmsNode c2 = createComponent("C2");
        // Test nested 
        c1.set("component", c2);
        List<CmsNode> componentsList = new ArrayList<CmsNode>();
        componentsList.add(createComponent("C3"));
        componentsList.add(createComponent("C4"));
        componentsList.add(createComponent("C5"));


        CmsNode n1 = new CmsNode("org.otherobjects.cms.jcr.TestObject");
        n1.setJcrPath("/news.html");
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

        persistenceManager.insert(n1);
        persistenceManager.save();

        CmsNode ns2 = (CmsNode) persistenceManager.getObject("/news.html");
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
        persistenceManager.update(n1);
        persistenceManager.save();

        ns2 = (CmsNode) persistenceManager.getObject("/news.html");
        assertEquals(n1.get("testString"), ns2.get("testString"));
        assertEquals(c1.get("name"), ns2.get("testComponent.name"));
        assertEquals(c2.get("name"), ns2.get("testComponent.component.name"));
        assertEquals(r1.get("name"), ns2.get("testReference.name"));
        assertEquals(stringsList.size(), ((List) ns2.get("testStringsList")).size());
        assertEquals(stringsList.get(1), ((List) ns2.get("testStringsList")).get(1));
        assertEquals(componentsList.size(), ((List) ns2.get("testComponentsList")).size());
        //assertEquals(componentsList.get(1), ((List) ns2.get("testComponentsList")).get(1));
        assertEquals(referencesList.size(), ((List) ns2.get("testReferencesList")).size());
        //assertEquals(referencesList.get(1), ((List) ns2.get("testReferencesList")).get(1));
    }

    private CmsNode createReference(String name)
    {
        CmsNode r = new CmsNode("org.otherobjects.cms.jcr.TestReferenceObject");
        r.setJcrPath("/" + name + ".html");
        r.set("name", name +" Name");
        persistenceManager.insert(r);
        r = (CmsNode) persistenceManager.getObject("/" + name + ".html");
        return r;
    }

    private CmsNode createComponent(String name)
    {
        CmsNode c = new CmsNode("org.otherobjects.cms.jcr.TestComponentObject");
        c.set("name", name + " Name");
        return c;
    }
}
