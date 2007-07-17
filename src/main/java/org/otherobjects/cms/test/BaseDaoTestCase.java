package org.otherobjects.cms.test;

import java.util.ResourceBundle;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * Base class for running DAO tests.
 * @author mraible
 */
public abstract class BaseDaoTestCase extends AbstractTransactionalDataSourceSpringContextTests {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected ResourceBundle rb;

    protected String[] getConfigLocations() {
        setAutowireMode(AUTOWIRE_BY_NAME);
        return new String[] {
                "file:src/test/resources/applicationContext-resources.xml",
                "file:src/main/resources/otherobjects.resources/config/applicationContext-dao.xml",
            };
    }

    /**
     * Create a HibernateTemplate from the SessionFactory and call flush() and clear() on it.
     * Designed to be used after "save" methods in tests: http://issues.appfuse.org/browse/APF-178.
     */
    protected void flush() {
        HibernateTemplate hibernateTemplate =
                new HibernateTemplate((SessionFactory) applicationContext.getBean("sessionFactory"));
        hibernateTemplate.flush();
        hibernateTemplate.clear();
    }
}
