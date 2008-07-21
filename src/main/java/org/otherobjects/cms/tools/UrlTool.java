package org.otherobjects.cms.tools;

import java.io.IOException;

import org.otherobjects.cms.Url;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;

public class UrlTool
{
    private OoResourceLoader ooResourceLoader;

    public UrlTool(OoResourceLoader ooResourceLoader)
    {
        this.ooResourceLoader = ooResourceLoader;
    }

    public String getUrl(String path)
    {
        Url url = new Url(path);
        return url.toString();
    }

    public String getResourceUrl(String resourcePath) throws IOException
    {
        OoResource resource = ooResourceLoader.getResource(resourcePath); 
        Url url = resource.getUrl();
        if(url!=null)
            return url.toString().replaceAll("//","/");
        else
            return "ERROR"; 
    }
}
