package org.otherobjects.cms.util;

import org.otherobjects.cms.bootstrap.OtherObjectsAdminUserCreator;
import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Displays the OTHERobjects startup message if all beans have been initialised successfully.
 * 
 * @author rich
 */
@Component
public class StartupMessage implements ApplicationListener<ContextRefreshedEvent>
{
    private final Logger logger = LoggerFactory.getLogger(StartupMessage.class);

    @Autowired
    private OtherObjectsConfigurator otherObjectsConfigurator;
    
    @Autowired(required = false)
    private OtherObjectsAdminUserCreator otherObjectsAdminUserCreator;

    public void onApplicationEvent(ContextRefreshedEvent e)
    {
        // Only do for root context refreshes
        if (((ContextRefreshedEvent)e).getApplicationContext().getParent() == null) 
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
            this.logger.info("**************************************************************");
            this.logger.info("");
            
            if (otherObjectsConfigurator != null)
            {
                this.logger.info("Version      : {}", otherObjectsConfigurator.getProperty("otherobjects.version"));
                this.logger.info("Environment  : {}", otherObjectsConfigurator.getEnvironmentName());
                this.logger.info("Private data : " + otherObjectsConfigurator.getProperty("site.private.data.path"));
                this.logger.info("Public data  : " + otherObjectsConfigurator.getProperty("site.public.data.path"));
            }
            else
                this.logger.warn("OtherObjectsConfigurator not correctly created.");
            this.logger.info("");

            if (otherObjectsAdminUserCreator != null && otherObjectsAdminUserCreator.getGeneratedAdminPassword() != null)
            {
                this.logger.info("Please visit /otherobjects/setup to configure admin user");
                this.logger.info("Temporary admin password: " + otherObjectsAdminUserCreator.getGeneratedAdminPassword());
                this.logger.info("");
            }

            //this.logger.info("Site URL     : " + new Url("/").getAbsoluteLink());
            //this.logger.info("Admin URL    : " + new Url("/otherobjects/").getAbsoluteLink());
            this.logger.info("**************************************************************");
            this.logger.info("**************************************************************");
        }
    }

    public void setOtherObjectsConfigurator(OtherObjectsConfigurator otherObjectsConfigurator)
    {
        this.otherObjectsConfigurator = otherObjectsConfigurator;
    }

    public void setOoAdminUserCreator(OtherObjectsAdminUserCreator ooAdminUserCreator)
    {
        this.otherObjectsAdminUserCreator = ooAdminUserCreator;
    }
}
