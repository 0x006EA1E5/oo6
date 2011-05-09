package org.otherobjects.cms.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class EventLogger implements ApplicationListener<ApplicationEvent>
{
    private final Logger logger = LoggerFactory.getLogger(EventLogger.class);

    public void onApplicationEvent(ApplicationEvent event)
    {
        logger.info("Event fired: " + event);
    }
}
