package org.otherobjects.cms.binding;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;

@SuppressWarnings("unchecked")
public class OORequestContext extends RequestContext
{
    private final Map model;
    private final HttpServletRequest request;

    public OORequestContext(HttpServletRequest request, ModelMap model)
    {
        super(request, model);
        this.request = request;
        this.model = model;
    }

    public OORequestContext(HttpServletRequest request, ServletContext servletContext, Map model)
    {
        super(request, servletContext, model);
        this.request = request;
        this.model = model;
    }

    @Override
    public BindStatus getBindStatus(String path) throws IllegalStateException
    {
        //path.replaceAll("resourceObject\\.", "");
        //path = "resourceObject.data["+path+"]";
        return new BindStatus(this, path, isDefaultHtmlEscape());
    }

    /**
     * Override this method to allow BindStatus to look in request attributes as well as the model.
     * This is essential since FormTool currently inserts form objects into the request attributes not
     * the model. 
     */
    @Override
    protected Object getModelObject(String modelName)
    {
        Object o = null;
        // Look in model first
        if (this.model != null)
        {
            o = this.model.get(modelName);
        }
        // Then try again in request attributes
        if(o==null)
        {
            o = this.request.getAttribute(modelName);
        }
        return o;
    }
}
