package org.otherobjects.cms.bootstrap;

import org.otherobjects.cms.model.User;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class JackrabbitPopulaterTest extends AbstractDependencyInjectionSpringContextTests
{
    private JackrabbitPopulater jackrabbitPopulater;
    private OtherObjectsAdminUserCreator userCreator;
    private DbSchemaInitialiser dbSchemaInitialiser;
    private JackrabbitInitialiser jackrabbitInitialiser;

    public void setJackrabbitInitialiser(JackrabbitInitialiser jackrabbitInitialiser)
    {
        this.jackrabbitInitialiser = jackrabbitInitialiser;
    }

    public void setUserCreator(OtherObjectsAdminUserCreator userCreator)
    {
        this.userCreator = userCreator;
    }

    public void setDbSchemaInitialiser(DbSchemaInitialiser dbSchemaInitialiser)
    {
        this.dbSchemaInitialiser = dbSchemaInitialiser;
    }

    public void setJackrabbitPopulater(JackrabbitPopulater jackrabbitPopulater)
    {
        this.jackrabbitPopulater = jackrabbitPopulater;
    }

    @Override
    protected String[] getConfigLocations()
    {
        return new String[]{"classpath:org/otherobjects/cms/bootstrap/jcr-populater-bootstrap-context.xml"};
    }

    public void testPopulateRepository() throws Exception
    {
        jackrabbitInitialiser.initialise();
        dbSchemaInitialiser.initialise(true);
        User adminUser = userCreator.createAdminUser();

        // Authenticate as new Admin user
        Authentication authentication = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        jackrabbitPopulater.populateRepository();

        System.out.println("Repository populated");
    }
}
