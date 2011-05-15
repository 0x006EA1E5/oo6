package org.otherobjects.cms.bootstrap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.CompactNodeTypeDefReader;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceMapping;
import org.apache.jackrabbit.spi.commons.nodetype.QDefinitionBuilderFactory;
import org.otherobjects.cms.jcr.OtherObjectsJackrabbitSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springmodules.jcr.JcrSessionFactory;

/**
 * Class to initialise a Jackrabbit repository. Set a resource containing the nodetypes you need registered. Will also setup both workspaces 
 * required by OTHERobjects. Will not populate the repository with any nodes/data.
 * 
 * Running {@link #initialise()} should be a safe thing to do on every startup. It doesn't modify the repository in a destructive way.
 * 
 * 
 * @author joerg
 *
 */
public class JackrabbitInitialiser
{
    private final Logger logger = LoggerFactory.getLogger(JackrabbitInitialiser.class);

    @javax.annotation.Resource
    private JcrSessionFactory jcrSessionFactory;
    private Resource nodeTypesConfig;

    public void initialise() throws Exception
    {
        Session session = this.jcrSessionFactory.getSession();
        // register namespaces and nodetypes
//        prepareRepository(session);
        Reader fileReader = new InputStreamReader(this.nodeTypesConfig.getInputStream());
        CndImporter.registerNodeTypes(fileReader, session);
        // Create live workspace
        createWorkspace(session, OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME);
    }

    /**
     * Sets up the JCR repository by configuring node types and creating the required workspaces.
     * @param session 
     * 
     * @throws RepositoryException
     * @throws ParseException
     * @throws InvalidNodeTypeDefException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void prepareRepository(Session session) throws RepositoryException, InvalidNodeTypeDefException, IOException, ParseException
    {
        Workspace ws = session.getWorkspace();
        NamespaceRegistry namespaceRegistry = ws.getNamespaceRegistry();

        // Read in the CND file
        Reader fileReader = new InputStreamReader(this.nodeTypesConfig.getInputStream());
        CompactNodeTypeDefReader cndReader = 
            new CompactNodeTypeDefReader(fileReader, "cnd input stream", new QDefinitionBuilderFactory()); //FIXME What is systemId for?

        // Register all un-registered namespaces
        NamespaceMapping namespaceMapping = (NamespaceMapping) cndReader.getNamespaceMapping();
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
        List ntdList = cndReader.getNodeTypeDefinitions();
        NodeTypeManagerImpl ntmgr = (NodeTypeManagerImpl) ws.getNodeTypeManager();
        NodeTypeRegistry ntreg = ntmgr.getNodeTypeRegistry();

        // Loop through the prepared NodeTypeDefs
        for (Iterator i = ntdList.iterator(); i.hasNext();)
        {
            // Get the NodeTypeDef...
            QNodeTypeDefinition ntd = (QNodeTypeDefinition) i.next();

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
    }

    private void createWorkspace(Session session, String workspaceName) throws RepositoryException
    {
        boolean workspaceExists = false;
        for (String w : session.getWorkspace().getAccessibleWorkspaceNames())
        {
            if (w.equals(workspaceName))
            {
                workspaceExists = true;
                break;
            }
        }

        // ... and create it if missing
        if (!workspaceExists)
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

}
