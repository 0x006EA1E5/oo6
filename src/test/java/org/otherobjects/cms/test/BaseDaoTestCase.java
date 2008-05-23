package org.otherobjects.cms.test;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;

import org.dbunit.DefaultDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.SessionFactory;
import org.otherobjects.cms.bootstrap.DbSchemaInitialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractTransactionalJUnit38SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = {"file:src/test/java/org/otherobjects/cms/bootstrap/db-bootstrap-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public abstract class BaseDaoTestCase extends AbstractTransactionalJUnit38SpringContextTests
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected SessionFactory sessionFactory;
    
    @Autowired
    DbSchemaInitialiser dbSchemaInitialiser;
    
    /**
     * Create a HibernateTemplate from the SessionFactory and call flush() and clear() on it.
     * Designed to be used after "save" methods in tests: http://issues.appfuse.org/browse/APF-178.
     */
    protected void flush()
    {
        HibernateTemplate hibernateTemplate = new HibernateTemplate((SessionFactory) applicationContext.getBean("sessionFactory"));
        hibernateTemplate.flush();
        hibernateTemplate.clear();
    }

    @SuppressWarnings("deprecation")
    protected void loadSeedData(String seedDataFile) throws Exception
    {
        // FIXME This only should happen once
        dbSchemaInitialiser.initialise(true);
        
        logger.debug("Loding seed data: {}", seedDataFile);
        IDatabaseConnection dbunitConn = null;

        try
        {
            Connection conn = sessionFactory.openSession().connection();
            conn.setAutoCommit(true);
            dbunitConn = new DatabaseConnection(conn);
            DatabaseConfig config = dbunitConn.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());

            IDataSet dataSet = loadDataSet(seedDataFile);
            DefaultDatabaseTester databaseTester = new DefaultDatabaseTester(dbunitConn);
            databaseTester.setDataSet(dataSet);
            databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
            databaseTester.onSetup();
        }
        catch (Exception ex)
        {
            logger.debug("Error seeding database", ex);
            throw ex;
        }
        finally
        {
            if (dbunitConn != null)
            {
                dbunitConn.close();
            }
        }
    }

    private IDataSet loadDataSet(String path) throws Exception
    {
        ClassLoader cl = this.getClass().getClassLoader();
        URL resource = cl.getResource(path);
        assertNotNull("Seed data file not found in classpath: " + path, resource);
        InputStream stream = resource.openStream();
        IDataSet data = new XmlDataSet(stream);
        return data;
    }
}
