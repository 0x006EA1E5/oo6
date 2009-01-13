package org.otherobjects.cms.config;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Pattern;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 * Global configuration access class that merges all configured properties files and then reshuffles them to use the correct environment specific 
 * settings. These are then used to override placeholders in the application context.
 * It also provides {@link Properties} like access to the processed properties.
 *  
 * @author joerg
 */
public class OtherObjectsConfigurator extends PropertyPlaceholderConfigurer implements ResourceLoaderAware
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String ENVIRONMENT_SYSPROP_KEY = "otherobjects.environment";

    public static final String SERVER_NAME_KEY = "site.server.name";
    public static final String CONTEXT_PATH_KEY = "site.server.context.path";
    public static final String DEFAULT_PORT_KEY = "site.server.default.port";
    public static final String DEFAULT_SECURE_PORT_KEY = "site.server.default.secureport";

    public static final String JNDI_BASE = "java:comp/env/";

    private String defaultEnvironment = "dev";

    private Properties mergedProperties = new Properties();

    @Override
    protected void convertProperties(Properties props)
    {
        // Force file ecnoding to be UTF-8
        // TODO Make this configurable?
        System.setProperty("file.encoding", "UTF-8");

        String environmentPrefix = getEnvironmentName();
        
        mergedProperties.setProperty("otherobjects.environment", environmentPrefix);

        Pattern pattern = Pattern.compile("^" + environmentPrefix + "\\.");

        // find all props starting with environmentPrefix and re-add them to the properties losing the prefix, thereby
        //effectively overriding global defaults
        for (Iterator<Object> it = props.keySet().iterator(); it.hasNext();)
        {
            Object key = it.next();
            Object value = props.get(key);
            if (pattern.matcher((String) key).lookingAt())
            {
                String newKey = pattern.matcher((String) key).replaceFirst("");
                mergedProperties.setProperty(newKey, (String) value);
                it.remove(); // remove prefixed keys from handed down props
            }
            else
            {
                mergedProperties.setProperty((String) key, (String) value);
            }
        }

        if (! environmentPrefix.equals("dev"))
            jndiOverride(mergedProperties);

        // make sure handed down props contains the same values as mergedProperties
        for (Object key : mergedProperties.keySet())
        {
            props.setProperty((String) key, (String) mergedProperties.get(key));
        }
        super.convertProperties(props);
    }

    private void jndiOverride(Properties props)
    {
        //TODO override properties with jndi settings if they exist
    }

    public String getEnvironmentName()
    {
        String environment;
        if (StringUtils.isNotBlank(System.getProperty(ENVIRONMENT_SYSPROP_KEY)))
            environment = System.getProperty(ENVIRONMENT_SYSPROP_KEY);
        else
            environment = defaultEnvironment;

        Assert.isTrue(environment.equals("dev") || environment.equals("test") || environment.equals("stage") || environment.equals("production"), "environment must be one of 'dev', 'test', 'stage' or 'production'");

        return environment;
    }

    public String getProperty(String key)
    {
        Assert.notNull(mergedProperties, "OtherObjectsConfigurator not initialised. Check your setup");
        return mergedProperties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue)
    {
        Assert.notNull(mergedProperties, "OtherObjectsConfigurator not initialised. Check your setup");
        return mergedProperties.getProperty(key, defaultValue);
    }

    public String listProperties()
    {
        if (mergedProperties != null)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mergedProperties.list(new PrintStream(baos));
            return baos.toString();
        }
        else
            return "";
    }

    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        try
        {
            Properties mavenProps = new Properties();
            Resource pomProps = resourceLoader.getResource("classpath:META-INF/maven/org.otherobjects.cms/otherobjects/pom.properties");
            if (pomProps.exists())
                mavenProps.load(pomProps.getInputStream());
            else
                throw new OtherObjectsException("Maven meta data can't be read.");
            if (mavenProps.containsKey("version"))
            {
                String appVersion = mavenProps.getProperty("version");
                mergedProperties.setProperty("otherobjects.version", appVersion);
            }
        }
        catch (Exception e)
        {
            logger.info("Couldn't set appVersion. Probably in local development mode");
            mergedProperties.setProperty("otherobjects.version", "Local");
        }
    }

    public void setProperty(String key, String value)
    {
        this.mergedProperties.setProperty(key, value);
    }

    protected void setDefaultEnvironment(String defaultEnvironment)
    {
        this.defaultEnvironment = defaultEnvironment;
    }
}
