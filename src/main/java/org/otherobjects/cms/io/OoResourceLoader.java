package org.otherobjects.cms.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.json.JSONException;
import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 * OoResource factory that knows about resolving virtual resource paths into real paths. 
 * Also knows about how to populate a resource's metadata.
 * 
 * @author joerg
 *
 */
public class OoResourceLoader implements ResourceLoaderAware, InitializingBean
{
    private final Logger logger = LoggerFactory.getLogger(OoResourceLoader.class);

    @javax.annotation.Resource
    private OtherObjectsConfigurator otherObjectsConfigurator;
    private ResourceLoader resourceLoader;
    private OoResourceMetaDataHelper metaDataHelper = new OoResourceMetaDataHelper();

    private String dataDirPath;
    private String uploadDirPath;

    public void afterPropertiesSet() throws Exception
    {
        // look up required config properties and implement fail fast behaviour
        dataDirPath = otherObjectsConfigurator.getProperty("site.public.data.path");
        //TODO Add methods to validate required properties to the configuratsor. Including paths or regexps.
        Assert.notNull(dataDirPath, "Property site.public.data.path has not been set. Add it to your properties file");

        uploadDirPath = otherObjectsConfigurator.getProperty("site.private.data.path") + "uploads/";
        
        // set some static props on DefautOoResource as a kind of poor DI
        DefaultOoResource.setDataDirPath(dataDirPath);

        DefaultOoResource.setDataBaseUrl(otherObjectsConfigurator.getProperty("site.public.data.url"));
        DefaultOoResource.setStaticBaseUrl(otherObjectsConfigurator.getProperty("site.static.url"));
    }

    public OoResource getResource(String path) throws IOException
    {
        ResourceInfo resourceInfo = preprocessPath(path);
        Resource resource = resourceLoader.getResource(resourceInfo.getPath());

        //wrap in ooResource
        DefaultOoResource ooResource = new DefaultOoResource(resource, path, resourceInfo.getPrefix(), resourceInfo.isWritable());
        if (resource.exists())
        {
            postprocessResource(ooResource);
        }
        else
        {
            preprocessNewResource(ooResource);
        }

        return ooResource;
    }

    /**
     * Returns list of all resouces at a path.
     * 
     * FIXME This only supports file resources at the moment. Also needs to be broken into
     * multiple pluggable implementations.
     * 
     * FIXME Needs test.
     * 
     * @param path
     * @return
     * @throws IOException
     */
    public List<OoResource> getResources(String path) throws IOException
    {
        Assert.isTrue(path.endsWith("/"), "Path must be to a directory (and therefore end with trailing slash): " + path);
        List<OoResource> matches = new ArrayList<OoResource>();
        DefaultOoResource resource = (DefaultOoResource) getResource(path);
        File f = resource.getFile();
        for (String s : f.list(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".ftl"); //FIXME this clearly doesn't belong here
            }
        }))
        {
            s = path + s;
            matches.add(getResource(s));
        }
        return matches;
    }

    protected void preprocessNewResource(DefaultOoResource ooResource) throws IOException
    {
        if (ooResource.isWritable())
        {
            // make all dirs in this path not yet existing
            File parentDir = ooResource.getFile().getParentFile();
            if (parentDir != null)
            {
                parentDir.mkdirs();
            }
        }
    }

    /**
     * FIXME Can we make this lazy?
     * 
     * @param ooResource
     */
    protected void postprocessResource(DefaultOoResource ooResource)
    {
        String metaData = metaDataHelper.readMetaDataString(ooResource);
        if (metaData != null)
        {
            try
            {
                OoResourceMetaData ooResourceMetaData = new OoResourceMetaData(metaData);
                ooResource.setMetaData(ooResourceMetaData);
            }
            catch (JSONException e)
            {
                logger.warn("Invalid metaData in " + ooResource.getPath() + ": " + e.getMessage());
            }
        }

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
        return new ResourceInfo(path, null, false);
    }

    /**
     * This method is closely coupled with {@link DefaultOoResource#getUrl()} - PLEASE keep them in sync
     * 
     * @param path
     * @param prefix
     * @return
     */
    private ResourceInfo rewritePathAccordingToPrefix(String path, OoResourcePathPrefix prefix)
    {
        StringBuffer buf = new StringBuffer();
        boolean writable = false;
        switch (prefix)
        {
            case CORE :
                buf.append(OoResourcePathPrefix.CORE.replacementFilePathPrefix());
                buf.append(path);
                break;
            case STATIC :
                buf.append(OoResourcePathPrefix.STATIC.replacementFilePathPrefix()); //FIXME this is probably wrong
                buf.append(path);
                break;
            case SITE :
                //buf.append(buf.append(OoResourcePathPrefix.SITE.replacementFilePathPrefix()););
                buf.append(path);
                break;
            case DATA :
                buf.append("file:");
                buf.append(dataDirPath);
                buf.append(path);
                writable = true; //only resources in the data path should be writable
                break;
            case UPLOAD :
                buf.append("file:");
                buf.append(uploadDirPath);
                buf.append(path);
                writable = true; //only resources in the data path should be writable
                break;

            default :
                // TODO Should we have a default case?

        }
        return new ResourceInfo(buf.toString(), prefix, writable);
    }

    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    protected class ResourceInfo
    {
        private String path;
        private boolean writable;
        private OoResourcePathPrefix prefix;

        public ResourceInfo(String path, OoResourcePathPrefix prefix, boolean writable)
        {
            this.path = path;
            this.prefix = prefix;
            this.writable = writable;
        }

        public OoResourcePathPrefix getPrefix()
        {
            return prefix;
        }

        public String getPath()
        {
            return (prefix!=null ? prefix.replacementFilePathPrefix() : "") + path;
        }

        public boolean isWritable()
        {
            return writable;
        }

    }

    public void setOtherObjectsConfigurator(OtherObjectsConfigurator otherObjectsConfigurator)
    {
        this.otherObjectsConfigurator = otherObjectsConfigurator;
    }

}
