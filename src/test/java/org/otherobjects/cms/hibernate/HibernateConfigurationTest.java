package org.otherobjects.cms.hibernate;

import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.persister.entity.EntityPersister;
import org.otherobjects.cms.test.BaseDaoTestCase;

public class HibernateConfigurationTest extends BaseDaoTestCase
{
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public void testColumnMapping() throws Exception
    {
        Session session = sessionFactory.openSession();
        try
        {
            Map metadata = sessionFactory.getAllClassMetadata();
            for (Object o : metadata.values())
            {
                EntityPersister persister = (EntityPersister) o;
                String className = persister.getEntityName();
                logger.debug("Trying select * from: " + className);
                Query q = session.createQuery("from " + className + " c");
                q.iterate();
                logger.debug("ok: " + className);
            }
        }
        finally
        {
            session.close();
        }
    }
}
