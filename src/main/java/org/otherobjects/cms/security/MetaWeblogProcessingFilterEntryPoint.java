package org.otherobjects.cms.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AuthenticationEntryPoint;

public class MetaWeblogProcessingFilterEntryPoint implements AuthenticationEntryPoint, InitializingBean, Ordered, ApplicationContextAware
{

    private static final int DEFAULT_ORDER = Integer.MAX_VALUE;
    private int order = DEFAULT_ORDER;
    private ApplicationContext applicationContext;

    public void commence(ServletRequest request, ServletResponse response, AuthenticationException authException) throws IOException, ServletException
    {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }

    public void afterPropertiesSet() throws Exception
    {
        if (order == DEFAULT_ORDER)
        {
            //FIXME OrderedUtils was from acegi and doesn't exist in version 2, need to find a different way of achieving this
            //OrderedUtils.copyOrderFromOtherClass(BasicProcessingFilter.class, applicationContext, this, true);
        }
    }

    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

}
