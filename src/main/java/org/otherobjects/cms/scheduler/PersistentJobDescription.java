package org.otherobjects.cms.scheduler;

import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.annotation.PropertyDefAnnotation;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.TypeDefAnnotation;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import flexjson.JSON;

@TypeDefAnnotation(jcrPath = "/scheduler", label = "Jobs", description = "Scheduled jobs", labelProperty = "jobName",superClassName = "org.otherobjects.cms.model.DynaNode")
public class PersistentJobDescription extends DynaNode {
	
	public final String GROUP_NAME = "default";
	
	private String jobName;
	private String groupName;
	private String jobClassName;
	private String cronExpression;
	private String groovyScript;
	
	private Class jobClass;
	
	@JSON(include = false)
	public Trigger getTrigger() 
	{
		try {
			return new CronTrigger((StringUtils.isNotBlank(jobName)) ? jobName : getId(), (StringUtils.isNotBlank(groupName)) ? groupName : GROUP_NAME, getCronExpression());
		} catch (ParseException e) {
			throw new OtherObjectsException("Couldn't parse cron expression when creating trigger", e);
		}
	}
	
	@JSON(include = false)
	public JobDetail getJobDetail()
	{
		JobDetail jobDetail = new JobDetail();
		jobDetail.setName((StringUtils.isNotBlank(jobName)) ? jobName : getId());
		jobDetail.setGroup((StringUtils.isNotBlank(groupName)) ? groupName : GROUP_NAME);
		
		if(isValidJobClass())
		{
			jobDetail.setJobClass(jobClass);
		}
		else
		{
			if(StringUtils.isBlank(groovyScript))
				throw new OtherObjectsException("Can't create JobDetail as no script is given");
			
			jobDetail.setJobClass(QuartzGroovyJobExecutor.class);
			
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put(QuartzGroovyJobExecutor.GROOVY_SCRIPT_KEY, groovyScript);
			
			jobDetail.setJobDataMap(jobDataMap);
		}
		return jobDetail;
	}
	
	private boolean isValidJobClass() {
		if(StringUtils.isBlank(jobClassName))
			return false;
		
		try {
			jobClass = Class.forName(jobClassName);
			return Job.class.isAssignableFrom(jobClass);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	@PropertyDefAnnotation(type = PropertyType.TEXT, label = "Name of job", order = 1)
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	@PropertyDefAnnotation(type = PropertyType.TEXT, label = "Name of group", order = 2)
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	@PropertyDefAnnotation(type = PropertyType.TEXT, label = "Name of job class", order = 3)
	public String getJobClassName() {
		return jobClassName;
	}
	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
	
	@PropertyDefAnnotation(type = PropertyType.TEXT, label = "Cron expression", order = 4, required = true)
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	
	@PropertyDefAnnotation(type = PropertyType.TEXT, label = "Groovy script source", order = 5, size = 500)
	public String getGroovyScript() {
		return groovyScript;
	}
	public void setGroovyScript(String groovyScript) {
		this.groovyScript = groovyScript;
	}
	
	
	
	
}
