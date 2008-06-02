package org.otherobjects.cms.bootstrap;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.types.TypeService;
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

    private UniversalJcrDao universalJcrDao;
    private Resource bootstrapScript;
    private TypeService typeService;

    public void populateRepository() throws Exception
    {
        runScript(bootstrapScript.getInputStream());
    }

    protected void runScript(InputStream is) throws IOException
    {
        logger.debug("Running setup scripts.");
        Binding binding = new Binding();
        binding.setProperty("universalJcrDao", universalJcrDao);
        binding.setProperty("typeService", typeService);
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

}
