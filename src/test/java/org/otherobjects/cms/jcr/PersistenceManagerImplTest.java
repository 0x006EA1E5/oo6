package org.otherobjects.cms.jcr;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

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

        TypeDef td = new TypeDef("site_TestObject");
        td.addProperty(new PropertyDef("testString", "string", null));
        td.addProperty(new PropertyDef("testText", "text", null));
        td.addProperty(new PropertyDef("testDate", "date", null));
        td.addProperty(new PropertyDef("testTime", "time", null));
        td.addProperty(new PropertyDef("testTimestamp", "timestamp", null));
        td.addProperty(new PropertyDef("testNumber", "number", null));
        td.addProperty(new PropertyDef("testDecimal", "decimal", null));
        td.addProperty(new PropertyDef("testBoolean", "boolean", null));
        
        types.registerType(td);        
    }

    @Override
    public void tearDown() throws Exception
    {
       super.tearDown();
    }

    public void testSaveCmsNode() throws RepositoryException
    {
        PersistenceManagerImpl persistenceManager = (PersistenceManagerImpl) getPersistenceManager(types);
        persistenceManager.setMapper(new TypeServiceMapperImpl(types));

        CmsNode ns = new CmsNode("site_TestObject");
        ns.setJcrPath("/news.html");
        ns.set("testString", "News Story 1");
        ns.set("testText", "Content of story");
        
        Calendar date = Calendar.getInstance();
        date.set(2000, 01, 02, 0, 0, 0);
        Calendar time = Calendar.getInstance();
        time.set(0, 0, 0, 03, 04, 05);
        Calendar timestamp = Calendar.getInstance();
        timestamp.set(2000, 01, 02, 03, 04, 05);
        ns.set("testDate", date.getTime());
        ns.set("testTime", time.getTime());
        ns.set("testTimestamp", timestamp.getTime());

        ns.set("testNumber", new Long(123456));
        ns.set("testDecimal", new BigDecimal("19.95"));
        ns.set("testBoolean", Boolean.TRUE);

        persistenceManager.insert(ns);
        persistenceManager.save();
        
        CmsNode ns2 = (CmsNode) persistenceManager.getObject("/news.html");
        assertEquals(ns.get("testString"), ns2.get("testString"));
        assertEquals(ns.get("testText"), ns2.get("testText"));
        assertEquals(ns.get("testDate"), ns2.get("testDate"));
        assertEquals(ns.get("testTime"), ns2.get("testTime"));
        assertEquals(ns.get("testTimestamp"), ns2.get("testTimestamp"));
        assertEquals(ns.get("testNumber"), ns2.get("testNumber"));
        assertEquals(ns.get("testDecimal"), ns2.get("testDecimal"));
        assertEquals(ns.get("testBoolean"), ns2.get("testBoolean"));
        assertNotNull(ns2.getId());
        
        ns.set("testString", "News Story 1.1");
        persistenceManager.update(ns);
        persistenceManager.save();
        
        ns2 = (CmsNode) persistenceManager.getObject("/news.html");
        assertEquals(ns.get("testString"), ns2.get("testString"));
    }

}
