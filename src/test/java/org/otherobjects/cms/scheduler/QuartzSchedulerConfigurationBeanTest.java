package org.otherobjects.cms.scheduler;

import org.springframework.test.AbstractSingleSpringContextTests;

public class QuartzSchedulerConfigurationBeanTest extends AbstractSingleSpringContextTests
{

    @Override
    protected String[] getConfigLocations()
    {
        return new String[]{"classpath:/org/otherobjects/cms/bootstrap/configurator.xml"};
    }

    public void testRefrestEvent() throws InterruptedException
    {
        // FIXME Test scheduler restart on job update/publish
        System.out.println("Scheduler test running");
//        ContextRefreshedEvent contextRefreshedEvent = new ContextRefreshedEvent(getApplicationContext());
//        QuartzSchedulerConfigurationBean q = new QuartzSchedulerConfigurationBean();
//        q.onApplicationEvent(contextRefreshedEvent);
    }

}
