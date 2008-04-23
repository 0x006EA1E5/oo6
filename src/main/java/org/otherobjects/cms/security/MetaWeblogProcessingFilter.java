package org.otherobjects.cms.security;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.AuthenticationDetailsSource;
import org.springframework.security.ui.AuthenticationDetailsSourceImpl;
import org.springframework.security.ui.AuthenticationEntryPoint;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.otherobjects.cms.servlet.TransparentRequestContentAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/**
 * This is a MetaWeblogApi specific spring security filter that inspects the xml-rpc workload and extracts username and password from that
 * to do authentication. The actual classes implementing the rpc methods then can safely ignore the credentials.
 * 
 * It assumes that the username is always the 2. and the password alyways the 3. argument in the payload
 * 
 * @author joerg
 *
 */
public class MetaWeblogProcessingFilter implements Filter, InitializingBean, Ordered
{
    private final Logger logger = LoggerFactory.getLogger(MetaWeblogProcessingFilter.class);

    private AuthenticationDetailsSource authenticationDetailsSource = new AuthenticationDetailsSourceImpl();
    private AuthenticationManager authenticationManager;
    private AuthenticationEntryPoint authenticationEntryPoint;

    public void destroy()
    {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if (!(request instanceof HttpServletRequest))
        {
            throw new ServletException("Can only process HttpServletRequest");
        }

        if (!(response instanceof HttpServletResponse))
        {
            throw new ServletException("Can only process HttpServletResponse");
        }

        HttpServletRequest httpRequest = new TransparentRequestContentAccessor((HttpServletRequest) request);

        UsernamePasswordAuthenticationToken authRequest = getUsernameAndPassword(httpRequest);

        authRequest.setDetails(authenticationDetailsSource.buildDetails(httpRequest));

        Authentication authResult;

        try
        {
            authResult = authenticationManager.authenticate(authRequest);
        }
        catch (AuthenticationException failed)
        {
            // Authentication failed
            if (logger.isDebugEnabled())
            {
                logger.debug("Authentication request for user: " + authRequest.getPrincipal() + " failed: " + failed.toString());
            }

            SecurityContextHolder.getContext().setAuthentication(null);

            authenticationEntryPoint.commence(request, response, failed);

            return;
        }

        // Authentication success
        if (logger.isDebugEnabled())
        {
            logger.debug("Authentication success: " + authResult.toString());
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);

        chain.doFilter(httpRequest, response);
    }

    private UsernamePasswordAuthenticationToken getUsernameAndPassword(ServletRequest request)
    {

        try
        {
            SAXReader parser = new SAXReader();
            Document doc = parser.read(request.getInputStream());
            if (logger.isDebugEnabled())
            {
                StringWriter sw = new StringWriter();
                XMLWriter writer = new XMLWriter(sw, new OutputFormat("    ", false));
                writer.write(doc);

                logger.debug("payload: " + sw.toString());
            }

            String username = doc.valueOf("/methodCall/params/param[2]/value/string");
            logger.debug("xml-rpc Username:" + username);
            if (username == null)
                username = doc.valueOf("/methodCall/params/param[2]/value");

            String password = doc.valueOf("/methodCall/params/param[3]/value/string");
            logger.debug("xml-rpc password: " + password);
            if (password == null)
                password = doc.valueOf("/methodCall/params/param[3]/value");

            logger.debug("xml-rpc Username:" + username + " password: " + password);

            if (password != null && username != null)
                return new UsernamePasswordAuthenticationToken(username, password);

        }
        catch (Exception e)
        {
            logger.debug("Couldn't read username/password from xml-rpc payload");
        }

        return null;
    }

    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(this.authenticationManager, "An AuthenticationManager is required");
    }

    public int getOrder()
    {
        return 0;
    }

    public AuthenticationManager getAuthenticationManager()
    {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationEntryPoint getAuthenticationEntryPoint()
    {
        return authenticationEntryPoint;
    }

    public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint)
    {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

}
