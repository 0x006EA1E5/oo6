package org.otherobjects.cms.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Filter that tries to match URLs of the form /controller/action/id and adds the id as a request parameter with the name 'resourceId'
 * Inspired by work done by carbonfive.com:
 *  http://www.carbonfive.com/community/archives/2007/06/parameterized_rest_urls_with_spring_mvc.html
 *  
 *  Activate by putting something like this in your web.xml
 *  
 *  <code>
 *      &lt;filter&gt;
 *          &lt;filter-name&gt;IdFromUrlFilter&lt;/filter-name&gt;
 *          &lt;filter-class&gt;org.otherobjects.cms.rest.IdFromUrlFilter&lt;/filter-class&gt;
 *      &lt;/filter&gt;
 *  
 *      &lt;filter-mapping&gt;
 *          &lt;filter-name&gt;IdFromUrlFilter&lt;/filter-name&gt;
 *          &lt;url-pattern&gt;/dipatcher/servlet/mapping/*&lt;/url-pattern&gt;
 *      &lt;/filter-mapping&gt;
 *  </code>
 * 
 * @author joerg
 *
 */
public class IdFromUrlFilter implements Filter
{
    private static final Pattern idPattern = Pattern.compile("^/(\\S*)/(\\S*)/(\\S*)");

    public void destroy()
    {
        //noop
    }

    /**
     * filters this request trying to match (g)rails style resource urls. Just chains through should any exception happen
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        boolean filterApplied = false;
        try
        {
            Matcher matcher = idPattern.matcher(((HttpServletRequest) request).getPathInfo());
            if (matcher.lookingAt())
            {
                chain.doFilter(new IdFromUrlRequest((HttpServletRequest) request, matcher.group(3)), response);
                filterApplied = true;
            }

        }
        catch (Exception e)
        {
            //noop
        }

        if (!filterApplied)
            chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException
    {
        // noop
    }

    class IdFromUrlRequest extends HttpServletRequestWrapper
    {
        public static final String RESOURCE_ID_PARAM_NAME = "resourceId";

        private Map<String, String[]> params;

        public void setResourceId(String resourceId)
        {
            this.params.put(RESOURCE_ID_PARAM_NAME, new String[]{resourceId});
        }

        public IdFromUrlRequest(HttpServletRequest request)
        {
            super(request);
            this.params = new HashMap<String, String[]>();
            this.params.putAll(super.getParameterMap()); //FIXME is this really a safe thing to do?
        }

        public IdFromUrlRequest(HttpServletRequest request, String resourceId)
        {
            this(request);
            setResourceId(resourceId);
        }

        @Override
        public String getParameter(String key)
        {
            String[] values = getParameterValues(key);
            if ((values == null) || (values.length < 1))
                return null;
            return values[0];
        }

        @Override
        public Map getParameterMap()
        {
            return Collections.unmodifiableMap(this.params);
        }

        @Override
        public Enumeration getParameterNames()
        {
            return new IteratorEnumeration(params.keySet().iterator());
        }

        @Override
        public String[] getParameterValues(String string)
        {
            return params.get(string);
        }

    }

    private class IteratorEnumeration implements Enumeration
    {
        private Iterator it = null;

        public IteratorEnumeration(Iterator it)
        {
            this.it = it;
        }

        public boolean hasMoreElements()
        {
            return it.hasNext();
        }

        public Object nextElement()
        {
            return it.next();
        }
    }
}
