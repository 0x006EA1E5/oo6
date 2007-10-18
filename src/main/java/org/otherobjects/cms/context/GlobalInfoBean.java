package org.otherobjects.cms.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.jndi.JndiTemplate;
import org.springframework.util.Assert;

public class GlobalInfoBean implements InitializingBean
{

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String SERVER_NAME_KEY = "site.server.name";
    public static final String CONTEXT_PATH_KEY = "site.server.context.path";

    public static final String JNDI_BASE = "java:comp/env/";
    public static final String JNDI_SERVER_NAME_PATH = "otherobjects/serverName";
    public static final String JNDI_CONTEXT_PATH_PATH = "otherobjects/contextPath";

    private static GlobalInfoBean instance;

    //private ApplicationContext applicationContext;
    private JndiTemplate jndiTemplate;
    private Resource[] propertyResources;
    private Properties resourceProperties;
    private Properties jndiProperties;

    public String getProperty(String propertName)
    {
        return jndiProperties.getProperty(propertName);
    }

    public void setPropertyResources(Resource[] propertyResources)
    {
        this.propertyResources = propertyResources;
    }

    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(propertyResources, "GlobalInfo must have a non null resource to be able to lookup required properties when JNDI fails");
        //Assert.notNull(applicationContext, "GlobalInfo needs a reference to the applicationContext");

        initialiseResourceProperties();
        initialiseJndiProperties();
        this.instance = this;
    }

    private void initialiseJndiProperties()
    {
        jndiProperties = new Properties(resourceProperties); // use the props read form the resources as default to be used when nothing was found in jndi
        try
        {
            jndiProperties.put(SERVER_NAME_KEY, jndiTemplate.lookup(JNDI_BASE + JNDI_SERVER_NAME_PATH));
        }
        catch (NamingException e)
        {
            logger.warn("Couldn't read jndi value for name: " + JNDI_SERVER_NAME_PATH);
        }

        try
        {
            jndiProperties.put(CONTEXT_PATH_KEY, jndiTemplate.lookup(JNDI_BASE + JNDI_CONTEXT_PATH_PATH));
        }
        catch (NamingException e)
        {
            logger.warn("Couldn't read jndi value for name: " + JNDI_CONTEXT_PATH_PATH);
        }
    }

    private void initialiseResourceProperties()
    {
        resourceProperties = new Properties();
        for (Resource resource : propertyResources)
        {
            if (resource.exists())
            {
                Properties part = new Properties();
                try
                {
                    part.load(resource.getInputStream());
                    resourceProperties.putAll(part);
                }
                catch (IOException e)
                {
                    logger.warn("Couldn't read resource: " + resource.getDescription());
                }
            }
        }
    }

    //    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    //    {
    //        this.applicationContext = applicationContext;
    //    }

    public String listProperties()
    {
        if (jndiProperties != null)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            jndiProperties.list(new PrintStream(baos));
            return baos.toString();
        }
        else
            return "";
    }

    public void setJndiTemplate(JndiTemplate jndiTemplate)
    {
        this.jndiTemplate = jndiTemplate;
    }

    /**
     * Get an initialised singleton instance of this Class 
     * @return
     */
    public static GlobalInfoBean getInstance()
    {
        return instance;
    }

}
