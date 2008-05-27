package org.otherobjects.cms.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CacheControlFilter implements Filter
{
    //private FilterConfig fc;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        HttpServletResponse response = (HttpServletResponse) res;
        response.addHeader("Expires", "Mon, 16 Jul 2007 23:00:00 GMT");
        response.addHeader("Cache-Control", "max-age=3600");
        chain.doFilter(req, response);
    }

    public void init(FilterConfig filterConfig)
    {
        // this.fc = filterConfig;
    }

    public void destroy()
    {
        // this.fc = null;
    }
}
