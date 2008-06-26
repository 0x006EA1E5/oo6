package org.otherobjects.cms.util;

import javax.annotation.Resource;

import org.otherobjects.cms.Url;
import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Displays the OTHERobjects startup message if all beans have been initialised successfully.
 * 
 * @author rich
 */
public class StartupMessage implements ApplicationListener
{
    private final Logger logger = LoggerFactory.getLogger(StartupMessage.class);

    @Resource
    private OtherObjectsConfigurator otherObjectsConfigurator;
    
    public void init()
    {
        System.err.println("*******************************************************");
        System.err.println("*************************** INIT **********************");
        System.err.println("*******************************************************");
    }
    
    public void destroy()
    {
        System.err.println("*******************************************************");
        System.err.println("************************* DESTROY *********************");
        System.err.println("*******************************************************");
    }
    
    public void onApplicationEvent(ApplicationEvent e)
    {
        // Only do for root context refreshes
        if (e instanceof ContextRefreshedEvent) // && ((ContextRefreshedEvent)e).getApplicationContext().getParent() == null) 
        {
            this.logger.info("**************************************************************");
            this.logger.info("**************************************************************");
            this.logger.info("  ___ _____ _   _ _____ ____       _     _           _       ");
            this.logger.info(" / _ \\_   _| | | | ____|  _ \\ ___ | |__ (_) ___  ___| |_ ___ ");
            this.logger.info("| | | || | | |_| |  _| | |_) / _ \\| '_ \\| |/ _ \\/ __| __/ __|");
            this.logger.info("| |_| || | |  _  | |___|  _ < (_) | |_) | |  __/ (__| |_\\__ \\");
            this.logger.info(" \\___/ |_| |_| |_|_____|_| \\_\\___/|_.__// |\\___|\\___|\\__|___/");
            this.logger.info("                                      |__/                ");
            this.logger.info("");
            this.logger.info("******************* Started up successfully ******************");
            this.logger.info("");
            if(otherObjectsConfigurator!=null)
                this.logger.info("Environment : {}", otherObjectsConfigurator.getEnvironmentName());
            else
                this.logger.info("Environment : WARNING! No configurator found.");
                
            this.logger.info("Site        : " + new Url("/").getAbsoluteLink());
            this.logger.info("Admin       : " + new Url("/otherobjects/").getAbsoluteLink());
            this.logger.info("");
            this.logger.info("**************************************************************");
            this.logger.info("**************************************************************");
        }
    }
}
