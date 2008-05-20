package org.otherobjects.cms.bootstrap;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class OtherObjectsAdminUserCreatorTest extends AbstractDependencyInjectionSpringContextTests
{
    private OtherObjectsAdminUserCreator userCreator;
    private DbSchemaInitialiser dbSchemaInitialiser;

    public void setDbSchemaInitialiser(DbSchemaInitialiser dbSchemaInitialiser)
    {
        this.dbSchemaInitialiser = dbSchemaInitialiser;
    }

    public void setUserCreator(OtherObjectsAdminUserCreator userCreator)
    {
        this.userCreator = userCreator;
    }

    @Override
    protected String[] getConfigLocations()
    {
        return new String[]{"classpath:org/otherobjects/cms/bootstrap/user-bootstrap-context.xml"};
    }

    public void testSetupAdminUser() throws Exception
    {
        dbSchemaInitialiser.initialise(true);
        userCreator.createAdminUser();
        System.out.println("admin user created");

    }
}
