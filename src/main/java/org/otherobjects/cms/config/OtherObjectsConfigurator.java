package org.otherobjects.cms.config;

import java.util.Properties;
import java.util.regex.Pattern;

import org.otherobjects.cms.util.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.Assert;

public class OtherObjectsConfigurator extends PropertyPlaceholderConfigurer
{
    public static final String ENVIRONMENT_SYSPROP_KEY = "otherobjects.environment";
    private Properties mergedProperties;

    @Override
    protected void convertProperties(Properties props)
    {
        String environmentPrefix = getEnvironmentPrefix();

        Pattern pattern = Pattern.compile("^" + environmentPrefix + "\\.");

        // find all props starting with environmentPrefix and re-add them to the properties losing the prefix, thereby
        //effectively overriding global defaults
        for (Object key : props.keySet())
        {
            if (pattern.matcher((String) key).lookingAt())
            {
                Object value = props.getProperty((String) key);
                String newKey = pattern.matcher((String) key).replaceFirst("");
                props.setProperty(newKey, (String) value);
            }
        }
        this.mergedProperties = props;
        // TODO Auto-generated method stub
        super.convertProperties(props);
    }

    private String getEnvironmentPrefix()
    {
        String environment;
        if (StringUtils.isNotBlank(System.getProperty(ENVIRONMENT_SYSPROP_KEY)))
            environment = System.getProperty(ENVIRONMENT_SYSPROP_KEY);
        else
            environment = "production";

        Assert.isTrue(environment.equals("dev") || environment.equals("test") || environment.equals("production"), "environment must be one of 'dev, 'test' or 'production'");

        return environment;
    }

    public String getProperty(String key)
    {
        Assert.notNull(mergedProperties, "OtherObjectsCOnfigurator not initialised. Check your setup");
        return mergedProperties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue)
    {
        Assert.notNull(mergedProperties, "OtherObjectsCOnfigurator not initialised. Check your setup");
        return mergedProperties.getProperty(key, defaultValue);
    }
}
