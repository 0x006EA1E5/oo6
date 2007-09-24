package org.otherobjects.cms.util;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.GenericApplicationContext;

import junit.framework.TestCase;

public class StartupMessageTest extends TestCase
{

    public void testOnApplicationEvent()
    {
        StartupMessage sm = new StartupMessage();
        ContextRefreshedEvent cre = new ContextRefreshedEvent(new GenericApplicationContext());
        sm.onApplicationEvent(cre);
        // TODO No error so probably ok!
    }

}
