package org.otherobjects.cms.jcr;

import java.util.Properties;

import javax.jcr.Repository;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.tools.ant.filters.StringInputStream;
import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.xml.sax.InputSource;

/**
 * FactoryBean for creating a JackRabbit (JCR-170) repository through Spring
 * configuration files. Use this factory bean when you have to manually
 * configure the repository; for retrieving the repository from JNDI use the
 * JndiObjectFactoryBean {@link org.springframework.jndi.JndiObjectFactoryBean}
 * 
 * Extended to allow configuration via OTHERobjects properties.
 * 
 * FIXME Do we need this now?
 * 
 * @version Copied for 0.9
 * @see org.springframework.jndi.JndiObjectFactoryBean
 * @author Costin Leau
 * @author rich
 * 
 */
public class RepositoryFactoryBean extends org.springmodules.jcr.RepositoryFactoryBean
{

    private OtherObjectsConfigurator otherObjectsConfigurator;

    /**
     * Default repository configuration file.
     */
    private static final String DEFAULT_CONF_FILE = "repository.xml";

    /**
     * Default repository directory.
     */
    private static final String DEFAULT_REP_DIR = ".";

    /**
     * Home directory for the repository.
     */
    private Resource homeDir;

    /**
     * Repository configuratin created through Spring.
     */
    private RepositoryConfig repositoryConfig;

    /**
     * @see org.springmodules.jcr.RepositoryFactoryBean#createRepository()
     */
    protected Repository createRepository() throws Exception
    {
        // return JackRabbit repository.
        return RepositoryImpl.create(repositoryConfig);
    }

    /**
     * @see org.springmodules.jcr.RepositoryFactoryBean#resolveConfigurationResource()
     */
    protected void resolveConfigurationResource() throws Exception
    {
        // read the configuration object
        if (repositoryConfig != null)
            return;

        if (this.configuration == null)
        {
            if (log.isDebugEnabled())
                log.debug("no configuration resource specified, using the default one:" + DEFAULT_CONF_FILE);
            configuration = new ClassPathResource(DEFAULT_CONF_FILE);
        }

        if (homeDir == null)
        {
            if (log.isDebugEnabled())
                log.debug("no repository home dir specified, using the default one:" + DEFAULT_REP_DIR);
            homeDir = new FileSystemResource(DEFAULT_REP_DIR);
        }

        // Replace variables with configurator
        String configXml = IOUtils.toString(configuration.getInputStream());
        configXml = replaceVariables(otherObjectsConfigurator.getProperties(), configXml, true);

        repositoryConfig = RepositoryConfig.create(new InputSource(new StringInputStream(configXml)), homeDir.getFile().getAbsolutePath());
    }
    
    
    /**
     * Performs variable replacement on the given string value.
     * Each <code>${...}</code> sequence within the given value is replaced
     * with the value of the named parser variable. If a variable is not found
     * in the properties an IllegalArgumentException is thrown unless
     * <code>ignoreMissing</code> is <code>true</code>. In the later case, the
     * missing variable is replaced by the empty string.
     *
     * @param value         the original value
     * @param ignoreMissing if <code>true</code>, missing variables are replaced
     *                      by the empty string.
     * @return value after variable replacements
     * @throws IllegalArgumentException if the replacement of a referenced
     *                                  variable is not found
     */
    public static String replaceVariables(Properties variables, String value,
                                   boolean ignoreMissing)
            throws IllegalArgumentException {
        StringBuffer result = new StringBuffer();

        // Value:
        // +--+-+--------+-+-----------------+
        // |  |p|-->     |q|-->              |
        // +--+-+--------+-+-----------------+
        int p = 0, q = value.indexOf("${");                // Find first ${
        while (q != -1) {
            result.append(value.substring(p, q));          // Text before ${
            p = q;
            q = value.indexOf("}", q + 2);                 // Find }
            if (q != -1) {
                String variable = value.substring(p + 2, q);
                String replacement = variables.getProperty(variable);
                if (replacement == null) {
                    if (ignoreMissing) {
                        replacement = value.substring(p, q+1);
                    } else {
                        throw new IllegalArgumentException(
                                "Replacement not found for ${" + variable + "}.");
                    }
                }
                result.append(replacement);
                p = q + 1;
                q = value.indexOf("${", p);                // Find next ${
            }
        }
        result.append(value.substring(p, value.length())); // Trailing text

        return result.toString();
    }

    /**
     * Shutdown method.
     * 
     */
    public void destroy() throws Exception
    {
        // force cast (but use only the interface)
        if (repository instanceof JackrabbitRepository)
            ((JackrabbitRepository) repository).shutdown();
    }

    /**
     * @return Returns the defaultRepDir.
     */
    public Resource getHomeDir()
    {
        return this.homeDir;
    }

    /**
     * @param defaultRepDir The defaultRepDir to set.
     */
    public void setHomeDir(Resource defaultRepDir)
    {
        this.homeDir = defaultRepDir;
    }

    /**
     * @return Returns the repositryConfig.
     */
    public RepositoryConfig getRepositoryConfig()
    {
        return this.repositoryConfig;
    }

    /**
     * @param repositoryConfig The repositryConfig to set.
     */
    public void setRepositoryConfig(RepositoryConfig repositoryConfig)
    {
        this.repositoryConfig = repositoryConfig;
    }

    public void setOtherObjectsConfigurator(OtherObjectsConfigurator otherObjectsConfigurator)
    {
        this.otherObjectsConfigurator = otherObjectsConfigurator;
    }

}
