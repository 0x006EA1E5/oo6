package org.otherobjects.cms.dao;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class DaoProvider implements ApplicationContextAware {
    
    @Resource
    private DaoService daoService;
    
    private ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
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
