package org.otherobjects.cms.test;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
    
    public BaseDaoTestCase() {
        // Since a ResourceBundle is not required for each class, just
        // do a simple check to see if one exists
        String className = this.getClass().getName();

        try {
            rb = ResourceBundle.getBundle(className);
        } catch (MissingResourceException mre) {
            //log.warn("No resource bundle found for: " + className);
        }
    }

    /**
     * Utility method to populate a javabean-style object with values
     * from a Properties file
     * @param obj the model object to populate
     * @return Object populated object
     * @throws Exception if BeanUtils fails to copy properly
     */
    protected Object populate(Object obj) throws Exception {
        // loop through all the beans methods and set its properties from
        // its .properties file
        Map<String, String> map = new HashMap<String, String>();

        for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            map.put(key, rb.getString(key));
        }

        BeanUtils.copyProperties(map, obj);

        return obj;
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
