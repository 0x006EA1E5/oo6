package org.otherobjects.cms.bootstrap;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class DbSchemaInitaliserTest extends AbstractDependencyInjectionSpringContextTests
{
    private DbSchemaInitialiser dbSchemaInitialiser;

    public void setDbSchemaInitialiser(DbSchemaInitialiser dbSchemaInitialiser)
    {
        this.dbSchemaInitialiser = dbSchemaInitialiser;
    }

    @Override
    protected String[] getConfigLocations()
    {
        return new String[]{"classpath:org/otherobjects/cms/bootstrap/db-bootstrap-context.xml"};
    }

    public void testDbInitialise() throws Exception
    {
        dbSchemaInitialiser.initialise(true);
        System.out.println("Db schema is initialised now");
    }
}
