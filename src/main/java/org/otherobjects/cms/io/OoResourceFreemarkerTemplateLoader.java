package org.otherobjects.cms.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import freemarker.cache.TemplateLoader;

public class OoResourceFreemarkerTemplateLoader implements TemplateLoader
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private OoResourceLoader ooResourceLoader;

    public OoResourceFreemarkerTemplateLoader(OoResourceLoader ooResourceLoader)
    {
        this.ooResourceLoader = ooResourceLoader;
    }

    public void closeTemplateSource(Object templateSource) throws IOException
    {
    }

    public Object findTemplateSource(String ooResourcePath) throws IOException
    {
        if (logger.isDebugEnabled())
            logger.debug("Looking for FreeMarker template with OoResource path [" + ooResourcePath + "]");

        OoResource ooResource = ooResourceLoader.getResource(ooResourcePath);
        return (ooResource.exists() ? ooResource : null);
    }

    public long getLastModified(Object templateSource)
    {
        if (templateSource instanceof FileSystemResource)
        {
            return ((FileSystemResource) templateSource).getFile().lastModified();
        }
        return -1;
    }

    public Reader getReader(Object templateSource, String encoding) throws IOException
    {
        if (templateSource instanceof OoResource)
        {
            OoResource ooResource = (OoResource) templateSource;
            return new InputStreamReader(ooResource.getInputStream(), encoding);
        }
        else
            throw new IOException("templateSource object " + templateSource.toString() + " isn't an OoResource");
    }

}
