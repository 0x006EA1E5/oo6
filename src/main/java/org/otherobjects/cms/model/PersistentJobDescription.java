package org.otherobjects.cms.model;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.scheduler.AbstractSpringQuartzJob;
import org.otherobjects.cms.scheduler.QuartzGroovyJobExecutor;
import org.otherobjects.cms.scheduler.QuartzSchedulerConfigurationBean;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;
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
 * All jobs get their UUID as the job name and the value of JOB_GROUP_NAME as group name. The triggers get the same name as the job and the group name TRIGGER_GROUP_NAME.
 * 
 * @author joerg
 *
 */
@Type(label = "Scheduled Job", description = "Scheduled Job", labelProperty = "jobName", superClassName = "org.otherobjects.cms.model.DynaNode")
public class PersistentJobDescription extends BaseNode
{

    public static final String JOB_GROUP_NAME = "jcr-job-group";
    public static final String TRIGGER_GROUP_NAME = "jcr-trigger-group";

    private String label;
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

    private Class<?> jobClass;

    private JobDetail jobDetail;
    private Trigger trigger;

    @JSON(include = false)
    public Trigger getTrigger()
    {
        if (trigger != null)
            return trigger;
        try
        {
            String cronExpression;
            if (StringUtils.isNotBlank(getCronExpression()))
                cronExpression = getCronExpression();
            else
                cronExpression = buildCronExpression();

            Trigger trigger = new CronTrigger(getId(), TRIGGER_GROUP_NAME, cronExpression);
            this.trigger = trigger;
            return trigger;
        }
        catch (Exception e)
        {
            return null; //FIXME should really throw an exception but interferes with dwr marshalling
        }
    }

    public String buildCronExpression() throws Exception
    {
        StringBuffer buf = new StringBuffer();
        String[] fields = {"second", "minute", "hour", "dayOfMonth", "month", "dayOfWeek", "year"};
        for (int i = 0; i < fields.length; i++)
        {
            String field = fields[i];
            String value = (String) PropertyUtils.getSimpleProperty(this, field);
            if (StringUtils.isNotBlank(value))
            {
                buf.append(value);
                buf.append(" ");
            }
            else
            {
                if (field.equals("dayOfWeek") && (StringUtils.isBlank(getDayOfMonth()) || getDayOfMonth().equals("*")))
                    buf.append("? ");
                if (!field.equals("year"))
                    buf.append("* ");
            }
        }
        return buf.toString().trim();
    }

    @JSON(include = false)
    public JobDetail getJobDetail()
    {
        if (this.jobDetail != null)
            return this.jobDetail;

        JobDetail jobDetail = new JobDetail();
        jobDetail.setName(getId());
        jobDetail.setGroup(JOB_GROUP_NAME);
        jobDetail.setVolatility(true);
        jobDetail.setDurability(false);
        jobDetail.setRequestsRecovery(false);

        JobDataMap jobDataMap = new JobDataMap();
        jobDetail.setJobDataMap(jobDataMap);

        // Put the username of the user having created this jobDescription into the jobData map so that the job can be run as that user
        jobDataMap.put(AbstractSpringQuartzJob.USER_ID_KEY, getUserId());

        if (isValidJobClass())
        {
            jobDetail.setJobClass(jobClass);
        }
        else
        {
            if (StringUtils.isBlank(groovyScript))
                return null; //FIXME should really throw an exception but interferes with dwr marshalling

            jobDetail.setJobClass(QuartzGroovyJobExecutor.class);
            jobDataMap.put(QuartzGroovyJobExecutor.GROOVY_SCRIPT_KEY, groovyScript);
        }
        this.jobDetail = jobDetail;
        return jobDetail;
    }

    public boolean isValid()
    {
        // Don't allow non-published jobs to run
        if (!this.isPublished())
            return false;

        // Make sure we have a valid trigger and job detail
        try
        {
            Trigger trigger = getTrigger();
            if (trigger == null)
                return false;

            JobDetail jobDetail = getJobDetail();
            if (jobDetail == null)
                return false;

            return true;
        }
        catch (OtherObjectsException e)
        {
            return false;
        }
    }

    private boolean isValidJobClass()
    {
        if (StringUtils.isBlank(jobClassName))
            return false;

        try
        {
            jobClass = Class.forName(jobClassName);
            return Job.class.isAssignableFrom(jobClass);
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }

    @Property(type = PropertyType.STRING, label = "Label", order = 1)
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(type = PropertyType.STRING, label = "Name of job class", order = 2)
    public String getJobClassName()
    {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName)
    {
        this.jobClassName = jobClassName;
    }

    @Property(type = PropertyType.STRING, label = "Cron expression", order = 3)
    public String getCronExpression()
    {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    @Property(type = PropertyType.TEXT, label = "Groovy script source", order = 4, size = 500)
    public String getGroovyScript()
    {
        return groovyScript;
    }

    public void setGroovyScript(String groovyScript)
    {
        this.groovyScript = groovyScript;
    }

    @Property(type = PropertyType.STRING, label = "Cron seconds", order = 5)
    public String getSecond()
    {
        return second;
    }

    public void setSecond(String second)
    {
        this.second = second;
    }

    @Property(type = PropertyType.STRING, label = "Cron minutes", order = 6)
    public String getMinute()
    {
        return minute;
    }

    public void setMinute(String minute)
    {
        this.minute = minute;
    }

    @Property(type = PropertyType.STRING, label = "Cron hours", order = 7)
    public String getHour()
    {
        return hour;
    }

    public void setHour(String hour)
    {
        this.hour = hour;
    }

    @Property(type = PropertyType.STRING, label = "Cron day of month", order = 8)
    public String getDayOfMonth()
    {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth)
    {
        this.dayOfMonth = dayOfMonth;
    }

    @Property(type = PropertyType.STRING, label = "Cron month", order = 9)
    public String getMonth()
    {
        return month;
    }

    public void setMonth(String month)
    {
        this.month = month;
    }

    @Property(type = PropertyType.STRING, label = "Cron day of week", order = 10)
    public String getDayOfWeek()
    {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek)
    {
        this.dayOfWeek = dayOfWeek;
    }

    @Property(type = PropertyType.STRING, label = "Cron year", order = 11)
    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

}
