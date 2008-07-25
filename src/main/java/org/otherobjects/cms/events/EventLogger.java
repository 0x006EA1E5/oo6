package org.otherobjects.cms.events;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class EventLogger implements ApplicationListener
{
    public void onApplicationEvent(ApplicationEvent event)
    {
        System.err.println(event);
    }
}
