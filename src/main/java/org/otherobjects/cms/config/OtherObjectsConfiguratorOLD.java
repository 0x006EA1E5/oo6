package org.otherobjects.cms.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.apache.commons.beanutils.MethodUtils;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.util.StringUtils;
import org.otherobjects.framework.OtherObjectsException;
import org.otherobjects.framework.SingletonBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;

/**
 * Global configuration access class that merges all configured properties files and then reshuffles 
 * them to use the correct environment specific settings. These are then used to override placeholders 
 * in the application context.<br/>
 * It also provides {@link Properties} like access to the processed properties.<br/><br/>
 *  
 * We have several sources for these properties:
 * <ul>
 *  <li>A list of resource locations passed to the bean</li>
 *  
 *  <li>Optionally, we can add a <em>ServletContext path relative</em> resource to this list. See
 *  {@link addServletContextPathRelativeProperties}</li>
 *  
 *  <li>JNDI settings (in namespace {@link jndiBase})</li>
 * </ul>
 * 
 *  <hr/>
 *  
 * @author joerg
 * @author geales
 */
public class OtherObjectsConfiguratorOLD extends PropertyPlaceholderConfigurer implements ResourceLoaderAware
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String ENVIRONMENT_SYSPROP_KEY = "otherobjects.environment";

    public static final String SERVER_NAME_KEY = "site.server.name";
    public static final String CONTEXT_PATH_KEY = "site.server.context.path";
    public static final String DEFAULT_PORT_KEY = "site.server.default.port";
    public static final String DEFAULT_SECURE_PORT_KEY = "site.server.default.secureport";

    public String jndiBase = "java:comp/env/otherobjects";
    
    private ServletContext servletContext;

    // redefine this here so we have visibility within this class
    protected Resource[] locations;

    private boolean useServletContextPathRelativeProperties = false;
    private String servletContextPathRelativePathPrefix = "file:./config"; 
    private String servletContextPathRelativePropertiesFileName = "otherobjects.properties"; 

    private String defaultEnvironment = "dev";

    private Properties mergedProperties = new Properties();

    @javax.annotation.Resource
    private BeanFactory beanFactory;
    
    
    private Object object;
    
    public void setObject(Object object)
    {
        this.object = object;
    }
    
    /** This is a bit of a hack to let us append a <code>.properties</code>
     * file based on the ServletContext path. It actually appends a resource to the 
     * <code>resources</code> list.
     * 
     * <hr/>
     * Date: 5 Jun 2010
     * 
     * @see addServletContextPathRelativeProperties
     * @author geales
     */
    @Override
    protected void loadProperties(Properties props) throws IOException {
        props.setProperty("site.server.context.path", getDefaultServletContextPath());
        props.setProperty("site.server.context.path.name", getDefaultServletContextPathName());
        addServletContextPathRelativeProperties();
        super.loadProperties(props);
        
    }
    
    /** Add a <code>.properties</code> file from a URL including the <em>default ServletContext path</em>. 
     *  This behaviour is only enabled by setting {@link useServletContextPathRelativeProperties}.<br/><br/>
     * 
     * For example, we may be running our webapp from a URL such as <em><code>http://example.com/our_web_app/</code></em>.
     * We can get the ServletContext path from the {@link ServletContext} injected by Spring (our Servlet container 
     * must support obtaining this path, as Tomcat does).<br/><br/>
     * 
     * We build a URL using this path to locate the <code>.properties</code> file, for example 
     * <em><code>file:../config/our_web_app/otherobjects.properties</code></em>.<br/><br/>
     * 
     * The full path takes the form
     * <em><code>{@link servletContextPathRelativePathPrefix}/ServletContext path/{@link servletContextPathRelativePropertiesFileName}</code></em>.<br/><br/>
     * 
     * Obviously, this is useful if we require multiple webapps to run in the same container with different 
     * configurations, <strong>and</strong> those configurations cannot be included in the code base. An example 
     * of this would be sensitive database settings which must be provided from outside the webapp for security 
     * reasons. Compare this to the alternative of JNDI (for example provided be Tomcat through 
     * <code>context.xml</code> files), which is tedious to maintain, especially in a development environment.<br/><br/>
     * 
     * Note, this file is added to the end of the list of properties resources, and therefore will override any
     * values set in earlier files. However, JNDI settings are applied only after all properties files have
     * been processed, so will override and settings in this file.<br/><br/> 
     * 
     * <hr/>
     * Date: 5 Jun 2010
     * 
     * @see getDefaultServletContextPath
     * @see servletContextPathRelativePathPrefix
     * @see servletContextPathRelativePropertiesFileName
     * @see useServletContextPathRelativeProperties
     *  
     * @author geales
     */
    protected void addServletContextPathRelativeProperties()
    {
        if(useServletContextPathRelativeProperties)
        {
            Resource contextPropertiesResource;
            try {
                String defaultContextPathName = getDefaultServletContextPathName();

                contextPropertiesResource = new UrlResource(servletContextPathRelativePathPrefix + 
                        defaultContextPathName + "/" + servletContextPathRelativePropertiesFileName);

                logger.info("Registering ServletContext path relative properties " + contextPropertiesResource.getDescription() + "");
                List<Resource> locationsList =  new ArrayList<Resource>(Arrays.asList(locations));
                locationsList.add(contextPropertiesResource);
                locations = locationsList.toArray(locations);
                super.setLocations(locations);

            } catch (MalformedURLException e) {
                logger.error("Failed to build URL for the Servlet Context path realitive properties file", e);
            }
        }
    }
    
    /** Attempt to get the ServletContext path from the {@link ServletContext} which Spring injected.<br/><br/>
     * 
     * N.B., this relies on a feature of Tomcat, not sure if other containers will support 
     * the method <code>getContextPath()</code>. If we fail to call this method, assume the 
     * ServletContext path is "".<br/><br/>
     * 
     * <hr/>
     * Date: 5 Jun 2010
     *
     * @see addServletContextPathRelativeProperties
     * @return the default path of the Context, or "" if we can't tell
     * @author geales
     */
    protected String getDefaultServletContextPath()
    {
        try {
            //servletContext.getServletContextName();
            //MethodUtils.invokeMethod(servletContext, "getRealPath", "");
            return (String) MethodUtils.invokeMethod(servletContext, "getContextPath", null);
        } catch (Exception e) {
            logger.warn("Unable to determine default Servlet Context path");
            //logger.debug("Servlet Context path retrieval failed", e);
        }
        // assume that a failure to call this method means we are in ROOT context  
        return "";
    }
    
    /** Helper - Gets NAME of the path, i.e., substitues <code>"/ROOT"</code> for 
     * <code>""</code> or <code>"/"</code><br/><br/>
     * 
     *  Useful for building disk paths, URLs etc
     *
     * <hr/>
     * Date: 7 Jun 2010
     *
     * @return
     * @author geales
     */
    protected String getDefaultServletContextPathName()
    {
        String defaultContextPath = getDefaultServletContextPath();
        if("".equals(defaultContextPath) || "/".equals(defaultContextPath))
            return "/ROOT";
        else
            return defaultContextPath;
    }
    /** Override properties with environment versions.<br/><br/>
     * 
     * This is (yet another) way of overriding properties.
     * 
     *
     * <hr/>
     * Date: 8 Jun 2010
     *
     * @see org.springframework.beans.factory.config.PropertyResourceConfigurer#convertProperties(java.util.Properties)
     * @param props
     * @author joerg
     * @author geales
     */
    @Override
    protected void convertProperties(Properties props)
    {
        // Force file ecnoding to be UTF-8
        // TODO Make this configurable?
        System.setProperty("file.encoding", "UTF-8");

        // Obtain our environment naming context
        String environmentPrefix = getEnvironmentName();
        
        mergedProperties.setProperty("otherobjects.environment", environmentPrefix);

        Pattern pattern = Pattern.compile("^" + environmentPrefix + "\\.");

        // find all props starting with environmentPrefix and re-add them to the properties losing the prefix, thereby
        // effectively overriding global defaults
        for (Iterator<Object> it = props.keySet().iterator(); it.hasNext();)
        {
            Object key = it.next();
            Object value = props.get(key);
            if (pattern.matcher((String) key).lookingAt())
            {
                String newKey = pattern.matcher((String) key).replaceFirst("");
                mergedProperties.setProperty(newKey, (String) value);
                it.remove(); // remove prefixed keys from handed down props
            }
            else
            {
                mergedProperties.setProperty((String) key, (String) value);
            }
        }
        
        // insert the context path into the public and private data paths.
        // i.e., convert './private-data' into './private-data/ROOT' if we are the root context
        if("true".equals(props.getProperty("site.private.data.path.context.relative")))
            mergedProperties.setProperty("site.private.data.path", props.getProperty("site.private.data.path")
                    + getDefaultServletContextPathName());
        // for public too
        if("true".equals(props.getProperty("site.public.data.path.context.relative")))
            mergedProperties.setProperty("site.public.data.path", props.getProperty("site.public.data.path")
                    + getDefaultServletContextPathName());
        
        
        // Finally, override with values from JNDI
        if (! environmentPrefix.equals("dev"))
            jndiOverride(mergedProperties);
        else
            logger.info("Skipping JNDI configuration options as we are in 'dev' environment");

        // make sure handed down props contains the same values as mergedProperties
        for (Object key : mergedProperties.keySet())
        {
            props.setProperty((String) key, (String) mergedProperties.get(key));
        }
        super.convertProperties(props);
    }

    /** Add and override properties with values from JNDI
     * 
     * @param props
     */
    private void jndiOverride(Properties props)
    {
        try {
            Context context = (Context) new InitialContext().lookup(jndiBase);
            NamingEnumeration<NameClassPair> names = context.list(""); 
            while(names.hasMore())
            {
                NameClassPair pair = names.next();

                String propertyName = pair.getName();
                String propertyValue = context.lookup(pair.getName()).toString();
                
                if(props.getProperty(propertyName) != null)
                    logger.info("Overriding property from JNDI: " + jndiBase + "/" + propertyName);
                else
                    logger.info("Adding property from JNDI: " + jndiBase + "/" + propertyName);

                props.setProperty(propertyName, propertyValue);

            }
        } catch (NameNotFoundException e) {
            logger.warn("JNDI properties not available. " + e.getExplanation());
        } catch (NamingException e) {
            logger.warn("Problem getting JNDI properties for '" + jndiBase + "'", e);
        }    
    }

    public String getEnvironmentName()
    {
        String environment;
        if (StringUtils.isNotBlank(System.getProperty(ENVIRONMENT_SYSPROP_KEY)))
            environment = System.getProperty(ENVIRONMENT_SYSPROP_KEY);
        else
            environment = defaultEnvironment;

        Assert.isTrue(environment.equals("dev") || environment.equals("test") || environment.equals("stage") || environment.equals("production"), "environment must be one of 'dev', 'test', 'stage' or 'production'");

        return environment;
    }

    public String getProperty(String key)
    {
        Assert.notNull(mergedProperties, "OtherObjectsConfigurator not initialised. Check your setup");
        return mergedProperties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue)
    {
        Assert.notNull(mergedProperties, "OtherObjectsConfigurator not initialised. Check your setup");
        return mergedProperties.getProperty(key, defaultValue);
    }

    public String listProperties()
    {
        if (mergedProperties != null)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mergedProperties.list(new PrintStream(baos));
            return baos.toString();
        }
        else
            return "";
    }

    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        try
        {
            Properties mavenProps = new Properties();
            Resource pomProps = resourceLoader.getResource("classpath:META-INF/maven/org.otherobjects.cms/otherobjects/pom.properties");
            if (pomProps.exists())
                mavenProps.load(pomProps.getInputStream());
            else
                throw new OtherObjectsException("Maven meta data can't be read.");
            if (mavenProps.containsKey("version"))
            {
                String appVersion = mavenProps.getProperty("version");
                mergedProperties.setProperty("otherobjects.version", appVersion);
            }
        }
        catch (Exception e)
        {
            logger.info("Couldn't set appVersion. Probably in local development mode");
            mergedProperties.setProperty("otherobjects.version", "Local");
        }
    }

    public void setProperty(String key, String value)
    {
        this.mergedProperties.setProperty(key, value);
    }

    protected void setDefaultEnvironment(String defaultEnvironment)
    {
        this.defaultEnvironment = defaultEnvironment;
    }

    public Properties getProperties()
    {
        return mergedProperties;
    }
    
    public void setLocations(Resource[] locations) {
        this.locations = locations;
        super.setLocations(locations);
    }

    public void setJndiBase(String jndiBase) {
        this.jndiBase = jndiBase;
    }
    
    public void setUseServletContextPathRelativeProperties(
            boolean useServletContextPathRelativeProperties) {
        this.useServletContextPathRelativeProperties = useServletContextPathRelativeProperties;
    }
    
    public void setServletContextPathRelativePathPrefix(
            String servletContextPathRelativePathPrefix) {
        this.servletContextPathRelativePathPrefix = servletContextPathRelativePathPrefix;
    }

    public void setContextPathRelativePropertiesFileName(
            String contextPathRelativePropertiesFileName) {
        this.servletContextPathRelativePropertiesFileName = contextPathRelativePropertiesFileName;
    }

    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    /**
     * @return the beanFactory
     */
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * @param beanFactory the beanFactory to set
     */
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


}
