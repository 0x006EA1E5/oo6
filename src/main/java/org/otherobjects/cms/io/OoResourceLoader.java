package org.otherobjects.cms.io;

import java.io.IOException;
import java.util.regex.Matcher;

import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

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
        ResourceInfo resourceInfo = preprocessPath(path);
        Resource resoure = resourceLoader.getResource(resourceInfo.getPath());

        //wrap in ooResource
        DefaultOoResource ooResource = new DefaultOoResource(resoure, resourceInfo.isWritable());
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
    protected ResourceInfo preprocessPath(String path)
    {
        for (OoResourcePathPrefix prefix : OoResourcePathPrefix.values())
        {
            Matcher matcher = prefix.pattern().matcher(path);
            if (matcher.lookingAt())
                return rewritePathAccordingToPrefix(matcher.replaceFirst(""), prefix);
        }
        // return unchanged if no prefix matched, defaults to non writable because with non-prefixed paths you shouldn't use OoResource specific stuff anyway
        return new ResourceInfo(path, false);
    }

    private ResourceInfo rewritePathAccordingToPrefix(String path, OoResourcePathPrefix prefix)
    {
        StringBuffer buf = new StringBuffer();
        boolean writable = false;
        switch (prefix)
        {
            case CORE :
                buf.append("otherobjects.resources");
                buf.append(path);
                break;
            case STATIC :
                buf.append("site.resources/static"); //FIXME this is probably wrong
                buf.append(path);
                break;
            case SITE :
                buf.append("site.resources");
                buf.append(path);
                break;
            case DATA :
                buf.append("file:");
                String dataDirPath = otherObjectsConfigurator.getProperty("site.data.dir.path");
                Assert.notNull(dataDirPath, "Property site.data.dir.path has not been set. Add it to site.properties");
                buf.append(dataDirPath);
                buf.append(path);
                writable = true; //only resources in the data path should be writable
                break;

        }
        return new ResourceInfo(buf.toString(), writable);
    }

    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    class ResourceInfo
    {
        private String path;
        private boolean writable;

        public ResourceInfo(String path, boolean writable)
        {
            this.path = path;
            this.writable = writable;
        }

        public String getPath()
        {
            return path;
        }

        public boolean isWritable()
        {
            return writable;
        }

    }

}
