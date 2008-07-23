package org.otherobjects.cms.events;

import javax.servlet.http.HttpServletRequest;

import org.otherobjects.cms.context.RequestContextUtils;
import org.otherobjects.cms.tools.FlashMessageTool;
import org.otherobjects.cms.util.FlashMessage;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.event.authentication.AbstractAuthenticationFailureEvent;

public class EventLogger implements ApplicationListener
{
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof AbstractAuthenticationFailureEvent)
        {
            Exception e = (Exception) ((AbstractAuthenticationFailureEvent) event).getException();
            System.err.println(e.getMessage());
//            HttpServletRequest httpServletRequest = RequestContextUtils.getHttpServletRequest();
//            FlashMessageTool flashMessageTool = new FlashMessageTool(httpServletRequest);
//            flashMessageTool.flashMessage(FlashMessage.ERROR, e.getMessage());
        }
        else
            System.err.println(event);
    }
}
