package org.otherobjects.cms.views;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class FreemarkerToolProvider implements ApplicationContextAware
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    public Map<String, Object> getTools()
    {
        Map<String, Object> tools = new HashMap<String, Object>();
        for (String beanName : applicationContext.getBeanDefinitionNames())
        {
            try
            {
                Object tool = applicationContext.getBean(beanName);
                Tool annotation = (Tool) tool.getClass().getAnnotation(Tool.class);
                if(annotation!=null)
                    tools.put(annotation.value(), tool);
            }
            catch (Exception e)
            {
                logger.warn("Error scanning bean for @Tool: " + beanName, e);
            }
        }
        return tools;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
