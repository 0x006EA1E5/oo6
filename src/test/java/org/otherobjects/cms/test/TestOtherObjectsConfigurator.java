package org.otherobjects.cms.test;

import org.otherobjects.framework.config.OtherObjectsConfigurator;

/**
 * 
 * @author joerg
 *
 */
public class TestOtherObjectsConfigurator extends OtherObjectsConfigurator
{
    public TestOtherObjectsConfigurator()
    {
        setDefaultEnvironment("test");
    }
    
    @Override
    public void setProperty(String key, String value)
    {
    }
}
