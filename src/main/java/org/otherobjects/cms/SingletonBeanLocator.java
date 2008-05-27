package org.otherobjects.cms;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;

public class SingletonBeanLocator implements BeanFactoryAware
{
    private static BeanFactory beanFactory;

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
        Assert.notNull(beanFactory, "BeanFactory not initialised.");
        return beanFactory.getBean(name);
    }

}
