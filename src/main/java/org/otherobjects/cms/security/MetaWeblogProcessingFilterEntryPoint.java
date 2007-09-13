package org.otherobjects.cms.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.ui.AuthenticationEntryPoint;
import org.acegisecurity.ui.basicauth.BasicProcessingFilter;
import org.acegisecurity.util.OrderedUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

public class MetaWeblogProcessingFilterEntryPoint implements
		AuthenticationEntryPoint, InitializingBean, Ordered,
		ApplicationContextAware {
	
	private static final int DEFAULT_ORDER = Integer.MAX_VALUE;
    private int order = DEFAULT_ORDER;
    private ApplicationContext applicationContext;
	
	public void commence(ServletRequest request, ServletResponse response,
			AuthenticationException authException) throws IOException,
			ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
	}

	public void afterPropertiesSet() throws Exception {
		if (order == DEFAULT_ORDER) {
			OrderedUtils.copyOrderFromOtherClass(BasicProcessingFilter.class, applicationContext, this, true);
		}
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
