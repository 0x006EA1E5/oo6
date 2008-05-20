package org.otherobjects.cms.bootstrap;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class JackrabbitInitialiserTest extends AbstractDependencyInjectionSpringContextTests
{
    private JackrabbitInitialiser jackrabbitInitialiser;

    public void setJackrabbitInitialiser(JackrabbitInitialiser jackrabbitInitialiser)
    {
        this.jackrabbitInitialiser = jackrabbitInitialiser;
    }

    @Override
    protected String[] getConfigLocations()
    {
        setAutowireMode(AUTOWIRE_BY_TYPE);
        return new String[]{"classpath:org/otherobjects/cms/bootstrap/jcr-bootstrap-context.xml"};
    }

    public void testInitialise() throws Exception
    {
        jackrabbitInitialiser.initialise();
        System.out.println("Repository should be initialised now!");
    }
}
