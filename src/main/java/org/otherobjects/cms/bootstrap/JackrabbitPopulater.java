package org.otherobjects.cms.bootstrap;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.ResourceScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Runs repository setup scripts.
 * 
 * TODO This should check the script version against a version number stored in the repo.
 * 
 * @author joerg
 */
public class JackrabbitPopulater
{
    private final Logger logger = LoggerFactory.getLogger(JackrabbitPopulater.class);

    private DaoService daoService;
    private UniversalJcrDao universalJcrDao;
    private Resource bootstrapScript;
    private Resource siteBootstrapScript;
    private TypeService typeService;
    private ResourceScanner resourceScanner;

    public void populateRepository() throws Exception
    {
        runScript(bootstrapScript.getInputStream());
        if(siteBootstrapScript != null && siteBootstrapScript.exists())
            runScript(siteBootstrapScript.getInputStream());
    }

    protected void runScript(InputStream is) throws IOException
    {
        logger.debug("Running setup scripts.");
        Binding binding = new Binding();
        binding.setProperty("universalJcrDao", universalJcrDao);
        binding.setProperty("typeService", typeService);
        // FIXME This should happen somewhere else probably
        binding.setProperty("resourceScanner", resourceScanner);
        binding.setProperty("daoService", daoService);
        GroovyShell shell = new GroovyShell(binding);
        String script = IOUtils.toString(is);
        shell.evaluate(script);
    }

    public void setUniversalJcrDao(UniversalJcrDao universalJcrDao)
    {
        this.universalJcrDao = universalJcrDao;
    }

    public void setBootstrapScript(Resource bootstrapScript)
    {
        this.bootstrapScript = bootstrapScript;
    }

    public TypeService getTypeService()
    {
        return typeService;
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    public Resource getSiteBootstrapScript()
    {
        return siteBootstrapScript;
    }

    public void setSiteBootstrapScript(Resource siteBootstrapScript)
    {
        this.siteBootstrapScript = siteBootstrapScript;
    }

    public void setResourceScanner(ResourceScanner resourceScanner)
    {
        this.resourceScanner = resourceScanner;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

}
