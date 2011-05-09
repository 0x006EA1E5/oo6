package org.otherobjects.cms.tools;

import java.io.IOException;
import java.util.Iterator;

import javax.annotation.Resource;

import org.otherobjects.cms.Url;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.util.JcrPathTool;
import org.otherobjects.cms.util.JcrPathTool.PathItem;
import org.otherobjects.cms.views.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Tool
public class UrlTool
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Resource
    private OoResourceLoader ooResourceLoader;

    public UrlTool()
    {
    }

    public String getUrl(String path)
    {
        Url url = new Url(path);
        return url.toString();
    }

    public String getResourceUrl(String resourcePath) throws IOException
    {
        try
        {
            // FIXME This is inefficient
            OoResource resource = ooResourceLoader.getResource(resourcePath); 
            Url url = resource.getUrl();
            if(url!=null)
                return url.toString().replaceAll("//","/");
            else
                return "{error}";
        }
        catch (Exception e)
        {
            logger.error("Error generating resource URL for path: " + resourcePath, e);
            return "{error}";
        } 
    }
    
    public String getAbsoluteUrl(String path) throws IOException
    {
        // FIXME This is inefficient
        Url url = new Url(path);
        return url.getAbsoluteLink();
    }
    
    public String getAbsoluteResourceUrl(String resourcePath) throws IOException
    {
        // FIXME This is inefficient
        OoResource resource = ooResourceLoader.getResource(resourcePath); 
        Url url = resource.getUrl();
        if(url!=null)
            return url.getAbsoluteLink().replaceAll("//","/");
        else
            return "{error}"; 
    }

    public String getJcrParentPath(String resourcePath) throws IOException
    {
        Iterator<PathItem> it = JcrPathTool.walkUpPath(resourcePath);       
        @SuppressWarnings("unused")
        PathItem pi1 = it.next();
        PathItem pi2 = it.next();
        return ( pi2.getPathUpToHere());        
    }

}
