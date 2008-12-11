package org.otherobjects.cms.views;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * Scans applicationContext for @Tool annotated components.
 * 
 * @author rich
 */
public class FreemarkerToolProvider implements ApplicationContextAware
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    public Map<String, Object> getTools()
    {
        Map<String, Object> tools = new HashMap<String, Object>();
        for (String beanName : this.applicationContext.getBeanDefinitionNames())
        {
            try
            {
                Object tool = this.applicationContext.getBean(beanName);
                Tool annotation = tool.getClass().getAnnotation(Tool.class);
                String name = StringUtils.isNotEmpty(annotation.value()) ? annotation.value() : generateName(tool.getClass().getName());
                if (annotation != null)
                {
                    tools.put(name, tool);
                }
            }
            catch (Exception e)
            {
                this.logger.warn("Error scanning bean for @Tool: " + beanName, e);
            }
        }
        return tools;
    }

    protected String generateName(String name)
    {
        Assert.notNull(name, "Can not generate name from null string.");
        // Strip off package
        name = StringUtils.substringAfterLast(name, ".");
        // Make first letter lowercase
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
