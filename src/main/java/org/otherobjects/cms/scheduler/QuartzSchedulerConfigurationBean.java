package org.otherobjects.cms.scheduler;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;


public class QuartzSchedulerConfigurationBean implements InitializingBean {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	Scheduler scheduler;
	
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}


	public void afterPropertiesSet() throws Exception {
		logger.debug("Scheduler: " + scheduler.getSchedulerName() + " " + scheduler.isStarted());
	}

}
