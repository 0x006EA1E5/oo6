//package org.otherobjects.cms.bootstrap;
//
//import org.otherobjects.cms.dao.UserDao;
//import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
//
//public class OoBootstrapperTest extends AbstractDependencyInjectionSpringContextTests
//{
//    private DbSchemaInitialiser dbSchemaInitialiser;
//    private JackrabbitInitialiser jackrabbitInitialiser;
//    private OtherObjectsAdminUserCreator otherObjectsAdminUserCreator;
//    private JackrabbitPopulater jackrabbitPopulater;
//    private UserDao userDao;
//
//    public void setDbSchemaInitialiser(DbSchemaInitialiser dbSchemaInitialiser)
//    {
//        this.dbSchemaInitialiser = dbSchemaInitialiser;
//    }
//
//    public void setJackrabbitInitialiser(JackrabbitInitialiser jackrabbitInitialiser)
//    {
//        this.jackrabbitInitialiser = jackrabbitInitialiser;
//    }
//
//    public void setOtherObjectsAdminUserCreator(OtherObjectsAdminUserCreator otherObjectsAdminUserCreator)
//    {
//        this.otherObjectsAdminUserCreator = otherObjectsAdminUserCreator;
//    }
//
//    public void setJackrabbitPopulater(JackrabbitPopulater jackrabbitPopulater)
//    {
//        this.jackrabbitPopulater = jackrabbitPopulater;
//    }
//
//    public void setUserDao(UserDao userDao)
//    {
//        this.userDao = userDao;
//    }
//
//    @Override
//    protected String[] getConfigLocations()
//    {
//        return new String[]{"classpath:org/otherobjects/cms/bootstrap/oo-bootstrap-context.xml"};
//    }
//
//    public void testBootstrap() throws Exception
//    {
//        // create ooBootstrapper manually 
//        OoBootstrapper ooBootstrapper = new OoBootstrapper();
//        ooBootstrapper.setDbSchemaInitialiser(dbSchemaInitialiser);
//        ooBootstrapper.setJackrabbitInitialiser(jackrabbitInitialiser);
//        ooBootstrapper.setJackrabbitPopulater(jackrabbitPopulater);
//        ooBootstrapper.setOtherObjectsAdminUserCreator(otherObjectsAdminUserCreator);
//        ooBootstrapper.setUserDao(userDao);
//
//        ooBootstrapper.bootstrap();
//
//        System.out.println("Bootstrap successfull!");
//    }
//}
