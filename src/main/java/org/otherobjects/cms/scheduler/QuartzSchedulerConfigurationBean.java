package org.otherobjects.cms.scheduler;
import java.util.List;

import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.TypeService;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Simple bean that configures the built-in quartz scheduler by querying for all {@link PersistentJobDescription} objects under a certain
 * repository path. Configuration happens at application context startup. 
 * @author joerg
 *
 */
public class QuartzSchedulerConfigurationBean implements InitializingBean {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Scheduler scheduler;
	private DynaNodeDao dynaNodeDao;
	private TypeService typeService;
	
	
	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}


	public void setDynaNodeDao(DynaNodeDao dynaNodeDao) {
		this.dynaNodeDao = dynaNodeDao;
	}


	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}


	public void afterPropertiesSet() throws Exception {
		logger.debug("Scheduler name: " + scheduler.getSchedulerName() + " started?:" + scheduler.isStarted());
		typeService.getType(PersistentJobDescription.class.getName());
		configure();
	}


	private void configure() throws Exception {
		List<DynaNode> jobs = dynaNodeDao.getAllByPath("/scheduler");
		for(DynaNode node : jobs)
		{
			if(node instanceof PersistentJobDescription)
			{
				PersistentJobDescription jobDescription = (PersistentJobDescription) node;
				if(jobDescription.isValid())
					scheduler.scheduleJob(jobDescription.getJobDetail(), jobDescription.getTrigger());
			}
		}
	}
	
	

}
