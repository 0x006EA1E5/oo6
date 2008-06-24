package org.otherobjects.cms.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.otherobjects.cms.Url;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;

import flexjson.JSON;

/**
 * Custom specialisation of Spring's {@link Resource} abstraction. Should always be obtained via {@link OoResourceLoader} bean.
 * 
 * @author joerg
 *
 */
public class DefaultOoResource extends AbstractResource implements OoResource
{
    private Resource resource;
    private String path;
    private boolean writable;
    private OoResourceMetaData metaData;
    private OoResourcePathPrefix prefix;

    private static String dataDirPath;
    private static String dataBaseUrl;
    private static String staticBaseUrl;

    public OoResourceMetaData getMetaData()
    {
        return metaData;
    }

    public void setMetaData(OoResourceMetaData metaData)
    {
        this.metaData = metaData;
    }

    DefaultOoResource(Resource resource, String path, OoResourcePathPrefix prefix, boolean writable)
    {
        this.resource = resource;
        this.path = path;
        this.prefix = prefix;
        this.writable = writable;
    }

    public String getDescription()
    {
        return resource.getDescription();
    }

    public OoResourcePathPrefix getPrefix()
    {
        return prefix;
    }

    @JSON(include = false)
    public InputStream getInputStream() throws IOException
    {
        return resource.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException
    {
        if (!writable)
        {
            throw new IOException("This resource is not writable!");
        }
        else
        {
            return new FileOutputStream(resource.getFile());
        }

    }

    @Override
    @JSON(include = false)
    public URL getURL() throws IOException
    {
        return resource.getURL();
    }

    @Override
    @JSON(include = false)
    public File getFile() throws IOException
    {
        return resource.getFile();
    }

    @Override
    public String getFilename() throws IllegalStateException
    {
        return resource.getFilename();
    }

    public String getPath()
    {
        return path;
    }

    /**
     * FIXME Don't like the name of this method. Or even if it works for enought resources to make it worth file.  
     * FIXME Actually might be better to just return URL instead.
     * 
     * @return
     */
    public String getFilePath()
    {
        try
        {
            String file = resource.getURI().toString();
            if (file.startsWith("file"))
                return file.substring(5);
            else
                return null;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public boolean isWritable()
    {
        return writable;
    }

    /**
     * This is closely coupled to {@link OoResourceLoader#rewritePathAccordingToPrefix} Keep in sync!!
     */
    @JSON(include = false)
    public Url getUrl()
    {
        switch (prefix)
        {
            case CORE :
                // core resources should be served by StaticResourceServlet by prefixing the class path with /static/ for Url purposes
                String corePathWithoutPrefix = OoResourcePathPrefix.CORE.pattern().matcher(path).replaceFirst("");
                return new Url("/static/" + OoResourcePathPrefix.CORE.replacementFilePathPrefix() + corePathWithoutPrefix);
            case STATIC :
                // as above
                String staticPathWithoutPrefix = OoResourcePathPrefix.STATIC.pattern().matcher(path).replaceFirst("");
                String staticUrlPath = (staticBaseUrl != null) ? staticBaseUrl : "/static/" + OoResourcePathPrefix.STATIC.replacementFilePathPrefix();
                return new Url(staticUrlPath + staticPathWithoutPrefix);
            case SITE :
                // 
                String sitePathWithoutPrefix = OoResourcePathPrefix.SITE.pattern().matcher(path).replaceFirst("");
                return new Url(sitePathWithoutPrefix.startsWith("/") ? sitePathWithoutPrefix : "/" + sitePathWithoutPrefix);
            case DATA :
                String dataPathWithoutPrefix = OoResourcePathPrefix.DATA.pattern().matcher(path).replaceFirst("");
                String dataUrlPath = (dataBaseUrl != null) ? dataBaseUrl : dataDirPath;
                return new Url(dataUrlPath + dataPathWithoutPrefix);
            case UPLOAD :
                return null; // we generally don't publicly allow access to just uploaded files
            default : // if the prefix is unknown we have no way of knowing how this resource might be URL addressable so return null
                return null;
        }
    }

    public static void setDataDirPath(String dataDirPath)
    {
        DefaultOoResource.dataDirPath = dataDirPath;
    }

    public static void setDataBaseUrl(String dataBaseUrl)
    {
        DefaultOoResource.dataBaseUrl = dataBaseUrl;
    }

    public static void setStaticBaseUrl(String staticBaseUrl)
    {
        DefaultOoResource.staticBaseUrl = staticBaseUrl;
    }

}
