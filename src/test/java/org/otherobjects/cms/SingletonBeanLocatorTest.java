package org.otherobjects.cms;

import junit.framework.TestCase;

import org.springframework.beans.factory.support.StaticListableBeanFactory;

public class SingletonBeanLocatorTest extends TestCase
{
    @SuppressWarnings("static-access")
    public void testGetBean()
    {
        StaticListableBeanFactory bf = new StaticListableBeanFactory();
        bf.addBean("test", this);
        
        //Test non-static access
        SingletonBeanLocator sbl = new SingletonBeanLocator();
        sbl.setBeanFactory(bf);
        assertNotNull(sbl.getBean("test"));
    }

}
