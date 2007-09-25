package org.otherobjects.cms.scheduler;

import org.apache.commons.beanutils.PropertyUtils;
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


/**
 * Class combining the information needed to define a quartz {@link JobDetail} and {@link Trigger}. Used by {@link QuartzSchedulerConfigurationBean} to configure the built-in
 * Quartz scheduler. The trigger can be configured by giving a full cron expression or by giving the individual fields that make up a cron expression. The full expression takes
 * precendence.
 * 
 * @author joerg
 *
 */
@TypeDefAnnotation(jcrPath = "/scheduler", label = "Scheduled Job", description = "Scheduled Job", labelProperty = "jobName",superClassName = "org.otherobjects.cms.model.DynaNode")
public class PersistentJobDescription extends DynaNode {
	
	public final String GROUP_NAME = "default";
	
	private String jobName;
	private String groupName;
	private String jobClassName;
	private String cronExpression;
	private String groovyScript;
	
	private String second;
	private String minute;
	private String hour;
	private String dayOfMonth;
	private String month;
	private String dayOfWeek;
	private String year;
	
	
	
	
	private Class jobClass;
	
	private JobDetail jobDetail;
	private Trigger trigger;
	
	@JSON(include = false)
	public Trigger getTrigger() 
	{
		if(trigger != null)
			return trigger;
		try {
			String cronExpression;
			if(StringUtils.isNotBlank(getCronExpression()))
				cronExpression = getCronExpression();
			else
				cronExpression = buildCronExpression();
				
			Trigger trigger = new CronTrigger((StringUtils.isNotBlank(jobName)) ? jobName : getId(), (StringUtils.isNotBlank(groupName)) ? groupName : GROUP_NAME, cronExpression);
			this.trigger = trigger;
			return trigger;
		} catch (Exception e) {
			return null; //FIXME should really throw an exception but interferes with dwr marshalling
		}
	}
	
	protected String buildCronExpression() throws Exception {
		StringBuffer buf = new StringBuffer();
		String[] fields = {"second", "minute", "hour", "dayOfMonth", "month", "dayOfWeek", "year"};
		for(int i = 0; i < fields.length; i++)
		{
			String field = fields[i];
			String value = (String) PropertyUtils.getSimpleProperty(this, field);
			if(StringUtils.isNotBlank(value))
			{
				buf.append(value);
				buf.append(" ");
			}
			else
			{
				if(field.equals("dayOfWeek") && (StringUtils.isBlank(getDayOfMonth()) || getDayOfMonth().equals("*") ))
					buf.append("? ");
				if(!field.equals("year"))
					buf.append("* ");
			}
		}
		return buf.toString().trim();
	}

	@JSON(include = false)
	public JobDetail getJobDetail()
	{
		if(this.jobDetail != null)
			return this.jobDetail;
		
		JobDetail jobDetail = new JobDetail();
		jobDetail.setName((StringUtils.isNotBlank(jobName)) ? jobName : getId());
		jobDetail.setGroup((StringUtils.isNotBlank(groupName)) ? groupName : GROUP_NAME);
		jobDetail.setVolatility(true);
		jobDetail.setDurability(false);
		jobDetail.setRequestsRecovery(false);
		
		if(isValidJobClass())
		{
			jobDetail.setJobClass(jobClass);
		}
		else
		{
			if(StringUtils.isBlank(groovyScript))
				return null; //FIXME should really throw an exception but interferes with dwr marshalling
			
			jobDetail.setJobClass(QuartzGroovyJobExecutor.class);
			
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put(QuartzGroovyJobExecutor.GROOVY_SCRIPT_KEY, groovyScript);
			
			jobDetail.setJobDataMap(jobDataMap);
		}
		this.jobDetail = jobDetail;
		return jobDetail;
	}
	
	public boolean isValid()
	{
		try{
			Trigger trigger = getTrigger();
			if(trigger == null)
				return false;
			
			JobDetail jobDetail = getJobDetail();
			if(jobDetail == null)
				return false;
			
			return true;
		}
		catch(OtherObjectsException e)
		{
			return false;
		}
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
	
	@PropertyDefAnnotation(type = PropertyType.STRING, label = "Name of job", order = 1)
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	@PropertyDefAnnotation(type = PropertyType.STRING, label = "Name of group", order = 2)
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	@PropertyDefAnnotation(type = PropertyType.STRING, label = "Name of job class", order = 3)
	public String getJobClassName() {
		return jobClassName;
	}
	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
	
	@PropertyDefAnnotation(type = PropertyType.STRING, label = "Cron expression", order = 4)
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
	
	@PropertyDefAnnotation(type = PropertyType.STRING, label = "Cron seconds", order = 6)
	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}
	
	@PropertyDefAnnotation(type = PropertyType.STRING, label = "Cron minutes", order = 7)
	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}
	
	@PropertyDefAnnotation(type = PropertyType.STRING, label = "Cron hours", order = 8)
	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}
	
	@PropertyDefAnnotation(type = PropertyType.STRING, label = "Cron day of month", order = 9)
	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	
	@PropertyDefAnnotation(type = PropertyType.STRING, label = "Cron month", order = 10)
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}
	
	@PropertyDefAnnotation(type = PropertyType.STRING, label = "Cron day of week", order = 11)
	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	@PropertyDefAnnotation(type = PropertyType.STRING, label = "Cron year", order = 12)
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
	
	
	
}
