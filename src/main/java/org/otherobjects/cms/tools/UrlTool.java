package org.otherobjects.cms.tools;

import java.io.IOException;

import javax.annotation.Resource;

import org.otherobjects.cms.Url;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.views.Tool;
import org.springframework.stereotype.Component;

@Component
@Tool
public class UrlTool
{
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
        // FIXME This is inefficient
        OoResource resource = ooResourceLoader.getResource(resourcePath); 
        Url url = resource.getUrl();
        if(url!=null)
            return url.toString().replaceAll("//","/");
        else
            return "{error}"; 
    }
}
