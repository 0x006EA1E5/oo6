package org.otherobjects.cms.bootstrap;

import javax.annotation.Resource;

import org.otherobjects.cms.util.LoggerUtils;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Level;

@Component
public class DbSchemaInitialiser {
    
    @Resource
    private LocalSessionFactoryBean sessionFactoryBean;

    public void setSessionFactoryBean(LocalSessionFactoryBean sessionFactoryBean)
    {
        this.sessionFactoryBean = sessionFactoryBean;
    }

    public void initialise(boolean dropFirst) throws Exception
    {
        if (dropFirst)
        {
            try
            {
                // Temporarily turn off warning logging during drops
                // We expect warnings so don't need report them
                Class<?> loggedClass = sessionFactoryBean.getClass();
                Level originalLoggerLevel = LoggerUtils.getLoggerLevel(loggedClass);
                LoggerUtils.setLoggerLevel(loggedClass, Level.ERROR);
                sessionFactoryBean.dropDatabaseSchema();
                LoggerUtils.setLoggerLevel(loggedClass, originalLoggerLevel);
            }
            catch (Exception e)
            {
                // do nothing if can't drop
            }
        }
        sessionFactoryBean.createDatabaseSchema();
    }
    
    public void update() throws Exception
    {
        sessionFactoryBean.updateDatabaseSchema();
    }
}
