package org.otherobjects.cms.binding;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;

public class OORequestContext extends RequestContext
{
    public OORequestContext(HttpServletRequest request, ModelMap model)
    {
        super(request, model);
    }
    
    @SuppressWarnings("unchecked")
    public OORequestContext(HttpServletRequest request, ServletContext servletContext, Map model)
    {
        super(request, servletContext, model);
    }

    @Override
    public BindStatus getBindStatus(String path) throws IllegalStateException
    {
        path.replaceAll("resourceObject\\.", "");
        path = "resourceObject.data["+path+"]";
        return new BindStatus(this, path, isDefaultHtmlEscape());
    }
}
