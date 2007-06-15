/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.otherobjects.cms.jcr;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeTypeManager;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.compact.CompactNodeTypeDefReader;
import org.apache.jackrabbit.core.nodetype.compact.ParseException;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.persistence.PersistenceManager;
import org.apache.jackrabbit.ocm.persistence.impl.PersistenceManagerImpl;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.jackrabbit.ocm.reflection.ReflectionUtils;
import org.apache.jackrabbit.ocm.repository.RepositoryUtil;
import org.otherobjects.cms.types.TypeService;
import org.xml.sax.ContentHandler;

/**
 * Base class for testcases. Provides priviledged access to the jcr test
 * repository.
 * 
 * @author <a href="mailto:okiessler@apache.org">Oliver Kiessler</a>
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 * @version $Id: Exp $
 */
public abstract class JcrTestBase extends TestCase
{

    private static final String OO_NODE_TYPES_FILE = "./configuration/oo-node-types.conf";

    private final static Log log = LogFactory.getLog(JcrTestBase.class);

    protected Session session;

    protected PersistenceManager persistenceManager;

    protected Mapper mapper;

    protected boolean isInit = false;

    /**
     * <p>
     * Defines the test case name for junit.
     * </p>
     * 
     * @param testName
     *            The test case name.
     */
    public JcrTestBase(String testName)
    {
        super(testName);
    }

    /**
     * Setting up the testcase.
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        try
        {
            super.setUp();

            if (!isInit)
            {
                // initPersistenceManager();
                // registerNodeTypes(getSession());
                isInit = true;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Getter for property persistenceManager.
     * @param types 
     * 
     * @return jcrSession
     */
    public PersistenceManager getPersistenceManager(TypeService types)
    {
        try
        {
            if (persistenceManager == null)
            {
                initPersistenceManager(types);
                registerNodeTypes(getSession());
            }
            return persistenceManager;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected void registerNodeTypes(Session session) throws InvalidNodeTypeDefException, RepositoryException, IOException, ParseException
    {
        // Register name spaces
        NamespaceRegistry namespaceRegistry = session.getWorkspace().getNamespaceRegistry();
        try
        {
            namespaceRegistry.getURI("oo");
        }
        catch (Exception e)
        {
            session.getWorkspace().getNamespaceRegistry().registerNamespace("oo", "http://www.otherobjects.org/system/oo6");
        }

        // Create a CompactNodeTypeDefReader
        FileReader fileReader = new FileReader(OO_NODE_TYPES_FILE);
        CompactNodeTypeDefReader cndReader = new CompactNodeTypeDefReader(fileReader, OO_NODE_TYPES_FILE);
        List types = cndReader.getNodeTypeDefs();

        Workspace workspace = session.getWorkspace();
        NodeTypeManager ntMgr = workspace.getNodeTypeManager();
        NodeTypeRegistry ntReg = ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();

        Iterator i = types.iterator();
        while (i.hasNext())
        {
            NodeTypeDef def = (NodeTypeDef) i.next();

            try
            {
                ntReg.getNodeTypeDef(def.getName());
            }
            catch (NoSuchNodeTypeException nsne)
            {
                // If not already registered then register custom node type
                ntReg.registerNodeType(def);
            }

        }
    }

    protected void initPersistenceManager(TypeService types) throws UnsupportedRepositoryOperationException, javax.jcr.RepositoryException, InvalidNodeTypeDefException, IOException, ParseException
    {
        Repository repository = RepositoryUtil.getRepository("repositoryTest");
        String[] files = {"./configuration/jcr-mapping.xml"};
        session = RepositoryUtil.login(repository, "superuser", "superuser");
        cleanUpRepository();
        registerNodeTypes(session);

//        TypeServiceMapperImpl mapper = new TypeServiceMapperImpl(types);
//        DefaultAtomicTypeConverterProvider converterProvider = new DefaultAtomicTypeConverterProvider();
//        Map atomicTypeConverters = converterProvider.getAtomicTypeConverters();
//        QueryManagerImpl queryManager = new QueryManagerImpl(mapper, atomicTypeConverters, session.getValueFactory());
//        persistenceManager = new PersistenceManagerImpl(mapper,queryManager,session);

        persistenceManager = new PersistenceManagerImpl(session, files);
    }

    /**
     * Setter for property jcrSession.
     * 
     * @param persistenceManager
     *            The persistence manager
     */
    public void setPersistenceManager(PersistenceManager persistenceManager)
    {
        this.persistenceManager = persistenceManager;
    }

    public void exportDocument(String filePath, String nodePath, boolean skipBinary, boolean noRecurse)
    {
        try
        {
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(filePath));
            ContentHandler handler = null;// new org.apache.xml.serialize.XMLSerializer(os, null).asContentHandler();
            session.exportDocumentView(nodePath, handler, skipBinary, noRecurse);
            os.flush();
            os.close();
        }
        catch (Exception e)
        {
            System.out.println("Impossible to export the content from : " + nodePath);
            e.printStackTrace();
        }
    }

    public void importDocument(String filePath, String nodePath)
    {
        try
        {
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(filePath));
            session.importXML(nodePath, is, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
            session.save();
            is.close();
        }
        catch (Exception e)
        {
            System.out.println("Impossible to import the content from : " + nodePath);
            e.printStackTrace();
        }

    }

    protected Session getSession()
    {
        return this.session;
    }

    public QueryManager getQueryManager()
    {
        return persistenceManager.getQueryManager();
    }

    @SuppressWarnings("unchecked")
    protected boolean contains(Collection result, String path, Class objectClass)
    {
        Iterator iterator = result.iterator();
        while (iterator.hasNext())
        {
            Object object = iterator.next();
            String itemPath = (String) ReflectionUtils.getNestedProperty(object, "path");
            if (itemPath.equals(path))
            {
                if (object.getClass() == objectClass)
                {
                    return true;
                }
                else
                {
                    return false;
                }

            }
        }
        return false;
    }

    protected void cleanUpRepository()
    {
        try
        {
            Session session = this.getSession();

            if (session != null)
            {
                NodeIterator nodeIterator = session.getRootNode().getNodes();
                while (nodeIterator.hasNext())
                {
                    Node node = nodeIterator.nextNode();
                    if (!node.getName().startsWith("jcr:"))
                    {
                        log.debug("tearDown - remove : " + node.getPath());
                        node.remove();
                    }
                }
                session.save();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}