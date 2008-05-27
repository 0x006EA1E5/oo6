package org.otherobjects.cms.scripting;

import org.otherobjects.cms.OtherObjectsException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Generic script resource resolver that can be used as is and configured as a spring managed bean or can be subclassed if you want to override the way it goes about 
 * finding resources in various places in the application.
 * 
 * The default behaviour is that it will first look for the resource in the filesystem in the WEB-INF/prefixPath folder and then - if includeClasspath is true - will try to 
 * find the resource in the prefixPath in the classpath. If it fails to find the resource in the site.resources it will look for it in otherobjects.resources.
 * @author joerg
 *
 */
public class GenericScriptResourceResolver implements ScriptResourceResolver, ApplicationContextAware, InitializingBean
{
    public static final String OTHEROBJECTS_MODULE = "otherobjects";
    public static final String SITE_MODULE = "site";

    private ApplicationContext applicationContext;

    private String prefixPath;
    private String fileSuffix;
    private boolean includeClasspath;

    public GenericScriptResourceResolver()
    {
        this.includeClasspath = true;
    }

    protected GenericScriptResourceResolver(String prefixPath, String fileSuffix, boolean includeClasspath)
    {
        this.prefixPath = prefixPath;
        this.fileSuffix = fileSuffix;
        this.includeClasspath = includeClasspath;
    }

    protected GenericScriptResourceResolver(String prefixPath, String fileSuffix)
    {
        this(prefixPath, fileSuffix, true);
    }

    public Resource resolveScriptName(String scriptName) throws Exception
    {
        Resource scriptResource = findResource(SITE_MODULE, scriptName);
        if (scriptResource.exists())
            return scriptResource;

        scriptResource = findResource(OTHEROBJECTS_MODULE, scriptName);
        if (scriptResource.exists())
            return scriptResource;
        else
            throw new OtherObjectsException("Couldn't resolve script with name " + scriptName);
    }

    protected Resource findResource(String module, String scriptName)
    {
        String scriptPath = "/" + module + ".resources/" + prefixPath + "/" + scriptName + fileSuffix;
        String fileScriptPath = "/WEB-INF" + scriptPath;
        Resource scriptResource = applicationContext.getResource(fileScriptPath);

        if (!includeClasspath || scriptResource.exists())
            return scriptResource;

        String classpathScriptPath = "classpath:" + scriptPath;
        scriptResource = applicationContext.getResource(classpathScriptPath);
        return scriptResource;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public String getPrefixPath()
    {
        return prefixPath;
    }

    public void setPrefixPath(String prefixPath)
    {
        this.prefixPath = prefixPath;
    }

    public String getFileSuffix()
    {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix)
    {
        this.fileSuffix = fileSuffix;
    }

    public boolean isIncludeClasspath()
    {
        return includeClasspath;
    }

    public void setIncludeClasspath(boolean includeClasspath)
    {
        this.includeClasspath = includeClasspath;
    }

    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(prefixPath, "The prefixPath property needs to be set");
        Assert.notNull(fileSuffix, "The fileSuffix property needs to be set");
    }

}
