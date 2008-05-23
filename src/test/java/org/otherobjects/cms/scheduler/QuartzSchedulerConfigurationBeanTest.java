package org.otherobjects.cms.scheduler;

import org.otherobjects.cms.test.BaseSharedContextTestCase;

//@ContextConfiguration(locations={"classpath:/org/otherobjects/cms/bootstrap/shared-test-context.xml"})
public class QuartzSchedulerConfigurationBeanTest extends BaseSharedContextTestCase
{
    public void testRefrestEvent() throws InterruptedException
    {
        // FIXME Test scheduler restart on job update/publish
        System.out.println("Scheduler test running");
//        ContextRefreshedEvent contextRefreshedEvent = new ContextRefreshedEvent(getApplicationContext());
//        QuartzSchedulerConfigurationBean q = new QuartzSchedulerConfigurationBean();
//        q.onApplicationEvent(contextRefreshedEvent);
    }

}
