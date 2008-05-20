package org.otherobjects.cms.bootstrap;

import org.otherobjects.cms.dao.UserDao;
import org.otherobjects.cms.model.User;
import org.springframework.beans.factory.InitializingBean;

public class OoBootstrapper implements InitializingBean
{
    public final static String DEFAULT_ADMIN_USER_NAME = "admin";

    private DbSchemaInitialiser dbSchemaInitialiser;
    private JackrabbitInitialiser jackrabbitInitialiser;
    private OtherObjectsAdminUserCreator otherObjectsAdminUserCreator;
    private JackrabbitPopulater jackrabbitPopulater;
    private UserDao userDao;

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void afterPropertiesSet() throws Exception
    {
        bootstrap();
    }

    public void bootstrap() throws Exception
    {
        //initialise jcr repository
        jackrabbitInitialiser.initialise();

        // initialise db schema
        if (schemaUpdateRequired())
            dbSchemaInitialiser.initialise(true);

        // create admin user if not yet existing
        User adminUser = getAdminUser();
        if (adminUser == null)
            adminUser = otherObjectsAdminUserCreator.createAdminUser();

        // populate repository with default infrastructure (folders, welcome page etc.)
        if (repositoryPopulationRequired())
            jackrabbitPopulater.populateRepository();

    }

    private boolean repositoryPopulationRequired()
    {
        // TODO Auto-generated method stub
        return true;
    }

    private User getAdminUser()
    {
        return (User) userDao.loadUserByUsername(DEFAULT_ADMIN_USER_NAME);
    }

    private boolean schemaUpdateRequired()
    {
        //TODO can we somehow find out whether we need to update the schema?
        return true;
    }

    public void setDbSchemaInitialiser(DbSchemaInitialiser dbSchemaInitialiser)
    {
        this.dbSchemaInitialiser = dbSchemaInitialiser;
    }

    public void setJackrabbitInitialiser(JackrabbitInitialiser jackrabbitInitialiser)
    {
        this.jackrabbitInitialiser = jackrabbitInitialiser;
    }

    public void setOtherObjectsAdminUserCreator(OtherObjectsAdminUserCreator otherObjectsAdminUserCreator)
    {
        this.otherObjectsAdminUserCreator = otherObjectsAdminUserCreator;
    }

    public void setJackrabbitPopulater(JackrabbitPopulater jackrabbitPopulater)
    {
        this.jackrabbitPopulater = jackrabbitPopulater;
    }

}
