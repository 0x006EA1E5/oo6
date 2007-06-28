package org.otherobjects.cms.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.jcr.RepositoryException;

import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.test.BaseJcrTestCase;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;

public class DynaNodeDaoJackrabbitTest extends BaseJcrTestCase
{
    private DynaNodeDao dynaNodeDao;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        dynaNodeDao = (DynaNodeDao) getApplicationContext().getBean("dynaNodeDao");
        TypeService typeService = (TypeService) getApplicationContext().getBean("typeService");
        setupTypesService(typeService);
    }

    private void setupTypesService(TypeService typeService)
    {
        TypeDef td = new TypeDef("org.otherobjects.Dyna.jcr.TestObject");
        td.addProperty(new PropertyDef("testString", "string", null, null));
        td.addProperty(new PropertyDef("testText", "text", null, null));
        td.addProperty(new PropertyDef("testDate", "date", null, null));
        td.addProperty(new PropertyDef("testTime", "time", null, null));
        td.addProperty(new PropertyDef("testTimestamp", "timestamp", null, null));
        td.addProperty(new PropertyDef("testNumber", "number", null, null));
        td.addProperty(new PropertyDef("testDecimal", "decimal", null, null));
        td.addProperty(new PropertyDef("testBoolean", "boolean", null, null));
        td.addProperty(new PropertyDef("testReference", "reference", "org.otherobjects.Dyna.jcr.TestReferenceObject", null));
        td.addProperty(new PropertyDef("testComponent", "component", "org.otherobjects.Dyna.jcr.TestComponentObject", null));
        td.addProperty(new PropertyDef("testStringsList", "string", null, "list"));
        td.addProperty(new PropertyDef("testComponentsList", "component", null, "list"));
        td.addProperty(new PropertyDef("testReferencesList", "reference", null, "list"));
        typeService.registerType(td);

        TypeDef td2 = new TypeDef("org.otherobjects.Dyna.jcr.TestReferenceObject");
        td2.addProperty(new PropertyDef("name", "string", null, null));
        typeService.registerType(td2);

        TypeDef td3 = new TypeDef("org.otherobjects.Dyna.jcr.TestComponentObject");
        td3.addProperty(new PropertyDef("name", "string", null, null));
        td3.addProperty(new PropertyDef("component", "component", "org.otherobjects.Dyna.jcr.TestComponentObject", null));
        typeService.registerType(td3);

    }

    public void testGet()
    {
        DynaNode node = dynaNodeDao.getByPath("/site/about/about-us.html");
        assertNotNull(node);
        assertEquals("About us", node.get("title"));
    }

    public void testRemove()
    {
        DynaNode node = dynaNodeDao.getByPath("/site/about/about-us.html");
        assertNotNull(node);

        dynaNodeDao.remove(node.getId());
        assertNull(dynaNodeDao.get(node.getId()));
    }

    public void testExistsAtPath()
    {
        assertTrue(dynaNodeDao.existsAtPath("/site/about/about-us.html"));
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

    public void testExists()
    {
        DynaNode node = dynaNodeDao.getByPath("/site/about/about-us.html");
        assertTrue(dynaNodeDao.exists(node.getId()));
        assertFalse(dynaNodeDao.exists(node.getId().replaceAll("a", "b")));
        assertFalse(dynaNodeDao.exists(null));
    }

    @SuppressWarnings("unchecked")
    public void testSave() throws RepositoryException
    {
        DynaNode r1 = createReference("R1");
        List<DynaNode> referencesList = new ArrayList<DynaNode>();
        referencesList.add(createReference("R3"));
        referencesList.add(createReference("R4"));
        referencesList.add(createReference("R5"));

        DynaNode c1 = createComponent("C1");
        DynaNode c2 = createComponent("C2");
        // Test nested 
        c1.set("component", c2);
        List<DynaNode> componentsList = new ArrayList<DynaNode>();
        componentsList.add(createComponent("C3"));
        componentsList.add(createComponent("C4"));
        componentsList.add(createComponent("C5"));

        DynaNode n1 = new DynaNode("org.otherobjects.Dyna.jcr.TestObject");
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
        assertEquals(c2.get("name"), ns2.get("testComponent.component.name"));
        assertEquals(r1.get("name"), ns2.get("testReference.name"));
        assertEquals(stringsList.size(), ((List) ns2.get("testStringsList")).size());
        assertEquals(stringsList.get(1), ((List) ns2.get("testStringsList")).get(1));
        assertEquals(componentsList.size(), ((List) ns2.get("testComponentsList")).size());
        //assertEquals(componentsList.get(1), ((List) ns2.get("testComponentsList")).get(1));
        assertEquals(referencesList.size(), ((List) ns2.get("testReferencesList")).size());
        //assertEquals(referencesList.get(1), ((List) ns2.get("testReferencesList")).get(1));
    }

    private DynaNode createReference(String name)
    {
        DynaNode r = new DynaNode("org.otherobjects.Dyna.jcr.TestReferenceObject");
        r.setJcrPath("/" + name + ".html");
        r.set("name", name + " Name");
        dynaNodeDao.save(r);
        r = (DynaNode) dynaNodeDao.getByPath("/" + name + ".html");
        return r;
    }

    private DynaNode createComponent(String name)
    {
        DynaNode c = new DynaNode("org.otherobjects.Dyna.jcr.TestComponentObject");
        c.set("name", name + " Name");
        return c;
    }

}
