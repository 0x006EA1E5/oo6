package org.otherobjects.cms.security;

import java.util.Date;

import org.otherobjects.cms.model.User;
import org.otherobjects.cms.model.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;


public class LoginListener implements ApplicationListener<ApplicationEvent>
{
    private final Logger logger = LoggerFactory.getLogger(LoginListener.class);

    /** Interval during which a repeated login doesn't trigger a lastLogin update (required because of issue http://jira.springsource.org/browse/SEC-1050 ). */
    private static final long REPEAT_LOGIN_IGNORE_INTERVAL = 30 * 60 * 1000L;

    private UserDao userDao;

    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof InteractiveAuthenticationSuccessEvent)
        {
            InteractiveAuthenticationSuccessEvent e = (InteractiveAuthenticationSuccessEvent) event;

            // log successfull login if it is an form based or remembered login once per session
            if (e.getGeneratedBy().equals(UsernamePasswordAuthenticationFilter.class) || e.getGeneratedBy().equals(RememberMeAuthenticationFilter.class)) 
            {
                logSuccessfullLogin(e.getAuthentication(), e);
            }
        }
    }

    private void logSuccessfullLogin(Authentication authentication, InteractiveAuthenticationSuccessEvent e)
    {
        User user = (User) (authentication.getPrincipal());
        Date now = new Date();
        if (e.getGeneratedBy().equals(RememberMeAuthenticationFilter.class) && user.getLastLogin() != null && (now.getTime() - user.getLastLogin().getTime() < REPEAT_LOGIN_IGNORE_INTERVAL))
            return;

        if (e.getSource() instanceof AbstractAuthenticationToken && ((AbstractAuthenticationToken) e.getSource()).getDetails() instanceof WebAuthenticationDetails)
        {
            user.setLastIpAddress(((WebAuthenticationDetails) ((AbstractAuthenticationToken) e.getSource()).getDetails()).getRemoteAddress());
        }

        user.setLastLogin(now);
        userDao.save(user);
        logger.info("User " + authentication.getPrincipal() + " logged in!");
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
