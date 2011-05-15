package org.otherobjects.cms.util;

import javax.annotation.Resource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class StartupMessageTest {
    @Resource
    private StartupMessage sm;
    
    // ************************************************************************
    // Constructors, initialisation, cleanup
    // ************************************************************************
    
    /**
     * Set the {@link OtherObjectsConfigurator} environment to <code>test</code> if it is <code>null</code>.
     * 
     * <p>This effectively defaults us to test config, although we can override this by setting the system property
     * ourselves before this test suite is run. For example, we may want to set the environment to something like 
     * <code>ci-test</code> for continuous integration tests (i.e, Hudson/Jenkins automated builds).
     * 
     * <hr>
     * 
     * @see OtherObjectsConfigurator
     */
    @BeforeClass
    public static void before() {
        if(System.getProperty(OtherObjectsConfigurator.ENVIRONMENT_SYSPROP_KEY) == null) {
            System.setProperty(OtherObjectsConfigurator.ENVIRONMENT_SYSPROP_KEY, "test");
        }
    }   
    
    @Test
    public void testOnApplicationEvent()
    {
        ContextRefreshedEvent cre = new ContextRefreshedEvent(new GenericApplicationContext());
        sm.onApplicationEvent(cre);
        // TODO No error so probably ok!
    }
}
