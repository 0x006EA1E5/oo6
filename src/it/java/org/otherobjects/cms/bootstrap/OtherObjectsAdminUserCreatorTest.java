//package org.otherobjects.cms.bootstrap;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit38.AbstractTransactionalJUnit38SpringContextTests;
//import org.springframework.test.context.transaction.TransactionConfiguration;
//import org.springframework.transaction.annotation.Transactional;
//
//@ContextConfiguration(locations = {"classpath:org/otherobjects/cms/bootstrap/user-bootstrap-context.xml","classpath:org/otherobjects/cms/bootstrap/db-bootstrap-context.xml"})
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
//@Transactional
//public class OtherObjectsAdminUserCreatorTest extends AbstractTransactionalJUnit38SpringContextTests
//{
//    @Autowired
//    private OtherObjectsAdminUserCreator userCreator;
//    
//    @Autowired
//    private DbSchemaInitialiser dbSchemaInitialiser;
//
//    public void testSetupAdminUser() throws Exception
//    {
//        dbSchemaInitialiser.initialise(true);
//        userCreator.createAdminUser();
//        System.out.println("admin user created");
//
//    }
//}
