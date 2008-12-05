package org.otherobjects.cms.dao;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DaoProvider implements ApplicationContextAware
{
    private DaoService daoService;
    
    private ApplicationContext applicationContext;

    public void init()
    {
        for (String beanName : applicationContext.getBeanDefinitionNames())
        {
            try
            {
                Object dao = applicationContext.getBean(beanName);
                if(dao instanceof GenericDao)
                    daoService.addDao((GenericDao) dao);
            }
            catch (Exception e)
            {
            }
        }
    }
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }
}
