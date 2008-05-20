package org.otherobjects.cms.bootstrap;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class JackrabbitPopulater
{
    private final Logger logger = LoggerFactory.getLogger(JackrabbitPopulater.class);

    private UniversalJcrDao universalJcrDao;
    private Resource bootstrapScript;

    public void populateRepository() throws Exception
    {
        runScript(bootstrapScript.getInputStream());
    }

    protected void runScript(InputStream is) throws IOException
    {
        logger.debug("Running setup scripts.");
        Binding binding = new Binding();
        binding.setProperty("universalJcrDao", universalJcrDao);
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

}
