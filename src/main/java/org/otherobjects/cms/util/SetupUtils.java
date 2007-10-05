package org.otherobjects.cms.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.LoginException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.compact.CompactNodeTypeDefReader;
import org.apache.jackrabbit.core.nodetype.compact.ParseException;
import org.apache.jackrabbit.util.name.NamespaceMapping;
import org.otherobjects.cms.jcr.OtherObjectsJackrabbitSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springmodules.jcr.JcrSessionFactory;

/**
 * Bootstrap utility that runs on site start up. Ensures all initial config data 
 * is correctly initalised.
 * 
 * <p>This curretntly does this by runnig the script in <code>bootstrap-data/setup.groovy</code>.
 * 
 * @author rich
 */
public class SetupUtils
{
    private static final String STRUCTURE_VERSION_PROPERTY_NAME = "structureVersion";

    private final Logger logger = LoggerFactory.getLogger(SetupUtils.class);

    private AnnotationSessionFactoryBean sessionFactory;
    private JcrSessionFactory jcrSessionFactory;
    private Resource nodeTypesConfig;
    private Resource systemDataTypes;
    private Resource siteDataTypes;
    
    private boolean standalone = false;

    public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}

	public void setSystemDataTypes(Resource systemDataTypes)
    {
        this.systemDataTypes = systemDataTypes;
    }

    public void setSiteDataTypes(Resource siteDataTypes)
    {
        this.siteDataTypes = siteDataTypes;
    }

    /**
     * Run the bootstrap script.
     */
    public void bootstrap()
    {
        // FIXME Run on first startup only
        try
        {
            if (getRepositoryStructureVersion() == 0)
            {
                prepareRepository();
                updateSchema();
                setRepositoryStructureVersion(1001);
            }
        }
        catch (Exception e)
        {
            throw new BeanInitializationException("Could not bootstrap data.", e);
        }
    }

    private void updateSchema()
    {
        try
        {
            this.sessionFactory.dropDatabaseSchema();
        }
        catch (Exception e)
        {
            // do nothing if can't drop
        }
        this.sessionFactory.createDatabaseSchema();
    }

    /**
     * Sets up the JCR repository by configuring node types and creating the required workspaces.
     * 
     * @throws RepositoryException
     * @throws ParseException
     * @throws InvalidNodeTypeDefException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void prepareRepository() throws RepositoryException, ParseException, InvalidNodeTypeDefException, IOException
    {
        Session session = this.jcrSessionFactory.getSession();
        Workspace ws = session.getWorkspace();
        NamespaceRegistry namespaceRegistry = ws.getNamespaceRegistry();

        // Read in the CND file
        Reader fileReader = new InputStreamReader(this.nodeTypesConfig.getInputStream());
        CompactNodeTypeDefReader cndReader = new CompactNodeTypeDefReader(fileReader, "systemId"); //FIXME What is systemId for?

        // Register all un-registered namespaces
        NamespaceMapping namespaceMapping = cndReader.getNamespaceMapping();
        Map uriToPrefixMapping = namespaceMapping.getURIToPrefixMapping();
        Iterator iterator = uriToPrefixMapping.keySet().iterator();
        while (iterator.hasNext())
        {
            String nsURI = (String) iterator.next();
            String nsPrefix = (String) uriToPrefixMapping.get(nsURI);
            try
            {
                namespaceRegistry.getPrefix(nsURI);
                this.logger.info("Namespace " + nsPrefix + ":" + nsURI + " arleady exists.");
            }
            catch (Exception e)
            {
                // Doesn't exist so register it
                this.logger.info("Namespace " + nsPrefix + ":" + nsURI + " doesn't exist. Creating...");
                session.getWorkspace().getNamespaceRegistry().registerNamespace(nsPrefix, nsURI);
            }

        }

        // Get the List of NodeTypeDef objects
        List ntdList = cndReader.getNodeTypeDefs();
        NodeTypeManagerImpl ntmgr = (NodeTypeManagerImpl) ws.getNodeTypeManager();
        NodeTypeRegistry ntreg = ntmgr.getNodeTypeRegistry();

        // Loop through the prepared NodeTypeDefs
        for (Iterator i = ntdList.iterator(); i.hasNext();)
        {
            // Get the NodeTypeDef...
            NodeTypeDef ntd = (NodeTypeDef) i.next();

            try
            {
                ntreg.getNodeTypeDef(ntd.getName());
                this.logger.info("Node type " + ntd.getName() + " already registered.");
            }
            catch (Exception e)
            {
                // ...and register it
                this.logger.info("Registering node type " + ntd.getName());
                ntreg.registerNodeType(ntd);
            }
        }

        // Create live workspace
        createWorkspace(session, OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME);
        setupDataTypes();

        return;
    }

    protected void setupDataTypes() throws LoginException, RepositoryException, IOException
    {
        Session session = getSession(OtherObjectsJackrabbitSessionFactory.EDIT_WORKSPACE_NAME);
        Session liveSession = getSession(OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME);

        // System types
        session.getWorkspace().importXML("/", this.systemDataTypes.getInputStream(), ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
        session.save();
        liveSession.getWorkspace().importXML("/", this.systemDataTypes.getInputStream(), ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
        liveSession.save();

        //Site types
        if(!standalone)
        {
        	session.getWorkspace().importXML("/types", this.siteDataTypes.getInputStream(), ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
        	session.save();
        	liveSession.getWorkspace().importXML("/types", this.siteDataTypes.getInputStream(), ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
        	liveSession.save();
        }
    }

    protected Session getSession(String workspaceName) throws LoginException, RepositoryException
    {
        return ((OtherObjectsJackrabbitSessionFactory) this.jcrSessionFactory).getSession(workspaceName);
    }

    private void createWorkspace(Session session, String workspaceName) throws RepositoryException
    {
        boolean liveWorkspaceExists = false;
        for (String w : session.getWorkspace().getAccessibleWorkspaceNames())
        {
            if (w.equals(workspaceName))
            {
                liveWorkspaceExists = true;
                break;
            }
        }

        // ... and create it if missing
        if (!liveWorkspaceExists)
        {
            Workspace workspace = session.getWorkspace();
            ((WorkspaceImpl) workspace).createWorkspace(workspaceName);
        }
    }

    public void setJcrSessionFactory(JcrSessionFactory jcrSessionFactory)
    {
        this.jcrSessionFactory = jcrSessionFactory;
    }

    public void setNodeTypesConfig(Resource nodeTypesConfig)
    {
        this.nodeTypesConfig = nodeTypesConfig;
    }

    public void setSessionFactory(AnnotationSessionFactoryBean sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Returns the version number of JCR reposistory structure. This will typically increase
     * on every release of OTHERobjects. It is used during upgrades to determine which updates 
     * to apply.
     * 
     * @return
     * @throws RepositoryException 
     * @throws PathNotFoundException 
     */
    protected int getRepositoryStructureVersion() throws PathNotFoundException, RepositoryException
    {
        Session session = this.jcrSessionFactory.getSession();
        Node rootNode = session.getRootNode();

        if (!rootNode.hasProperty(STRUCTURE_VERSION_PROPERTY_NAME))
            return 0;
        else
            return (int) rootNode.getProperty(STRUCTURE_VERSION_PROPERTY_NAME).getLong();
    }

    protected void setRepositoryStructureVersion(int version) throws PathNotFoundException, RepositoryException
    {
        Session session = this.jcrSessionFactory.getSession();
        session.getRootNode().setProperty(STRUCTURE_VERSION_PROPERTY_NAME, version);
        session.save();
    }
}
