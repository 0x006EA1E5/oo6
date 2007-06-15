package org.otherobjects.cms.jcr;

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
        types = new TypeService();

        TypeDef td = new TypeDef("site_NewsStory");
        td.addProperty(new PropertyDef("title", "string", null));
        td.addProperty(new PropertyDef("content", "string", null));
        //td.addProperty(new PropertyDef("publicationTimestamp", "date", null));
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

        Date pubDate = Calendar.getInstance().getTime();
        
        CmsNode ns = new CmsNode("site_NewsStory");
        ns.setJcrPath("/news.html");
        ns.set("title", "News Story 1");
        ns.set("content", "Content of story");
        //ns.set("publicationTimestamp", pubDate);

        persistenceManager.insert(ns);
        persistenceManager.save();
        
        CmsNode ns2 = (CmsNode) persistenceManager.getObject("/news.html");
        assertEquals(ns.get("title"), ns2.get("title"));
        //assertEquals(ns.get("publicationTimestamp"), ns2.get("publicationTimestamp"));
        assertNotNull(ns2.getId());
        
        ns.set("title", "News Story 1.1");
        persistenceManager.update(ns);
        persistenceManager.save();
        
        ns2 = (CmsNode) persistenceManager.getObject("/news.html");
        assertEquals(ns.get("title"), ns2.get("title"));
    }

}
