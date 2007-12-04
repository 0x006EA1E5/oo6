package org.otherobjects.cms.scripting;

import org.otherobjects.cms.OtherObjectsException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

public abstract class AbstractScriptResourceResolver implements ScriptResourceResolver, ApplicationContextAware
{
    public final static String OTHEROBJECTS_MODULE = "otherobjects";
    public final static String SITE_MODULE = "site";

    private ApplicationContext applicationContext;

    private String prefixPath;
    private String fileSuffix;
    private boolean includeClasspath;

    protected AbstractScriptResourceResolver(String prefixPath, String fileSuffix, boolean includeClasspath)
    {
        this.prefixPath = prefixPath;
        this.fileSuffix = fileSuffix;
        this.includeClasspath = includeClasspath;
    }

    protected AbstractScriptResourceResolver(String prefixPath, String fileSuffix)
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

}
