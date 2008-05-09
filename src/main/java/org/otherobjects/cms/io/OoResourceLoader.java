package org.otherobjects.cms.io;

import java.io.IOException;
import java.util.regex.Matcher;

import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * 
 * @author joerg
 *
 */
public class OoResourceLoader implements ResourceLoaderAware, InitializingBean
{
    @javax.annotation.Resource
    private OtherObjectsConfigurator otherObjectsConfigurator;
    private ResourceLoader resourceLoader;

    public void afterPropertiesSet() throws Exception
    {

    }

    public OoResource getResource(String path) throws IOException
    {
        Resource resoure = resourceLoader.getResource(preprocessPath(path));

        //wrap in ooResource
        OoResource ooResource = new DefaultOoResource(resoure);
        if (resoure.exists())
        {
            postprocessResource(ooResource);
        }

        return ooResource;
    }

    protected void postprocessResource(OoResource ooResource)
    {
        // TODO Auto-generated method stub

    }

    /**
     * Transform the path into a path understandable by a Spring resource loader by stripping of the path prefix and modifying 
     * the path according to the stripped off prefix
     * 
     * @param path
     * @return
     */
    protected String preprocessPath(String path)
    {
        for (OoResourcePathPrefix prefix : OoResourcePathPrefix.values())
        {
            Matcher matcher = prefix.pattern().matcher(path);
            if (matcher.lookingAt())
                return rewritePathAccordingToPrefix(matcher.replaceFirst(""), prefix);
        }
        // return unchanged if no prefix matched
        return path;
    }

    private String rewritePathAccordingToPrefix(String replaceFirst, OoResourcePathPrefix prefix)
    {
        StringBuffer buf = new StringBuffer();
        switch (prefix)
        {
            case CORE :
                break;
            case STATIC :
                break;
            case SITE :
                break;
            case DATA :
                break;

        }
        return buf.toString();
    }

    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

}
