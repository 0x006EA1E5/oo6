package org.otherobjects.cms.context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Class to hold static methods to help getting access to application and servlet context information
 * from code that hasn't got direct access to a request object or an injected application context.
 * Only works from code that somehow runs in the context of a {@link DispatcherServlet} which should be almost all code.
 * @author joerg
 *
 */
public class RequestContextUtils {
	
	/**
	 * Get the context path of the current web application as reported by the {@link ServletContext} (preferable but only available in containers supporting Servlet API >= 2.5)
	 * or the {@link HttpServletRequest#getContextPath()}
	 * is either an empty String or starts with a slash and never ends with a slash
	 * @return contextPath or null if it can't be determined
	 */
	public static String getContextPath()
	{
		ServletContext context = getServletContext();
		if(context != null)
		{
			if(context.getMajorVersion() == 2 && context.getMinorVersion() > 5)
				try {
					return (String) PropertyUtils.getSimpleProperty(context, "contextPath");
				} catch (Exception e) {
				} 
			else
			{
				HttpServletRequest httpServletRequest = getHttpServletRequest();
				if(httpServletRequest != null)
					return httpServletRequest.getContextPath();
			}
		}
		return null;
	}
	
	/**
	 * Get the current {@link ServletContext}
	 * @return current ServletContext or null if it can't be determined
	 */
	public static ServletContext getServletContext() {
		WebApplicationContext webApplicationContext = getWebApplicationContext();
		if(webApplicationContext != null)
				return webApplicationContext.getServletContext();
		
		return null;
	}
	
	/**
	 * Get the current {@link WebApplicationContext} 
	 * @return current WebApplicationContext or null it it can't be determined
	 */
	public static WebApplicationContext getWebApplicationContext()
	{
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if(requestAttributes != null)
		{
			return (WebApplicationContext) requestAttributes.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, RequestAttributes.SCOPE_GLOBAL_SESSION);
		}
		return null;
	}
	
	/**
	 * Get the current {@link HttpServletRequest}
	 * @return current HttpServletRequest or null if it can't be determined
	 */
	public static HttpServletRequest getHttpServletRequest()
	{
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if(requestAttributes != null && requestAttributes instanceof ServletWebRequest)
			return ((ServletWebRequest)requestAttributes).getRequest();
		
		return null;
	}
}
