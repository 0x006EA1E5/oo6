package org.otherobjects.cms.bootstrap;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class JackrabbitInitialiserTest extends AbstractDependencyInjectionSpringContextTests
{
    @Override
    protected String[] getConfigLocations()
    {
        return new String[]{"classpath:org/otherobjects/cms/bootstrap/bootstrap-context.xml"};
    }

    public void testRun()
    {
        System.out.println("Repository should be initialised now!");
    }
}
