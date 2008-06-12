package org.otherobjects.cms;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;

public class SingletonBeanLocator implements BeanFactoryAware
{
    private static BeanFactory beanFactory;
    private static Map<String,Object> overrides = new HashMap<String,Object>();
    
    public void setBeanFactory(BeanFactory beanFactory)
    {
        SingletonBeanLocator.beanFactory = beanFactory;
    }

    public static void setStaticBeanFactory(BeanFactory beanFactory)
    {
        SingletonBeanLocator.beanFactory = beanFactory;
    }

    public static Object getBean(String name)
    {
        if(overrides.containsKey(name))
            return overrides.get(name);

        Assert.notNull(beanFactory, "BeanFactory not initialised.");
        return beanFactory.getBean(name);
    }
    
    public static void registerTestBean(String name, Object bean)
    {
        overrides.put(name, bean);
    }
}
