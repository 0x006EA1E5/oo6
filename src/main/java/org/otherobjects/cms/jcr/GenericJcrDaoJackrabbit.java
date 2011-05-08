package org.otherobjects.cms.jcr;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.ocm.exception.JcrMappingException;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.jackrabbit.ocm.spring.JcrMappingCallback;
import org.apache.jackrabbit.ocm.spring.JcrMappingTemplate;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.dao.PagedList;
import org.otherobjects.cms.dao.PagedListImpl;
import org.otherobjects.cms.events.PublishEvent;
import org.otherobjects.cms.model.Audited;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

/**
 * Base class for all JCR DAOs.
 * 
 * <p>FIXME Add rangeselector to all methods
 * <p>FIXME Better method naming
 *  
 * 
 * @author rich
 */
public class GenericJcrDaoJackrabbit<T extends CmsNode & Audited> implements GenericJcrDao<T>, ApplicationContextAware
{
    private final Logger logger = LoggerFactory.getLogger(GenericJcrDaoJackrabbit.class);
    private Class<T> persistentClass = null;

    // To execute JCR queries
    private JcrMappingTemplate jcrMappingTemplate;

    // To get access to live/edit sessions for publishing
    private OtherObjectsJackrabbitSessionFactory sessionFactory;

    // To allow publishing of events
    private ApplicationContext applicationContext;

    // To get access to rule engine
    //    private RuleExecutor ruleExecutor;

    private Validator validator;

    private Cache cache;

    public void setCacheManager(CacheManager cacheManager)
    {
        this.cache = cacheManager.getCache("org.otherobjects.cms.JCR_CACHE");
    }

    public GenericJcrDaoJackrabbit()
    {
    }

    public GenericJcrDaoJackrabbit(Class<T> persistentClass)
    {
        this.persistentClass = persistentClass;
    }

    public String getPersistentClassName()
    {
        return this.persistentClass.getName();
    }

    public T save(T object)
    {
        return save(object, true);
    }

    public T save(T object, boolean validate)
    {
        if (!canSaveWithRepositoryCheck(object))
            throw new OtherObjectsException("Can't save BaseNode '" + object + "' because it has either in use by a different user or has been changed in the repository since being loaded");

        if (validate)
        {
            Errors errors = new BeanPropertyBindingResult(object, "target");
            if (validator != null)
                validator.validate(object, errors);
            if (errors.getErrorCount() > 0)
            {
                for (Object e : errors.getAllErrors())
                {
                    logger.warn("Validation error: " + e);
                }
                throw new OtherObjectsException(object + " couldn't be validated and therefore didn't get saved.");
            }
        }
        updateAuditInfo(object, null);
        return saveInternal(object, false);
    }

    protected T saveInternal(T baseNode, boolean publishStatus)
    {
        baseNode.setPublished(publishStatus);
        return saveSimple(baseNode, false);
    }

    protected T saveSimple(T object, boolean validate)
    {
        CmsNode existingObj = null;
        // PERF Extra lookup required to check path change
        if (object.getId() != null)
            existingObj = get(object.getId());
        if(object.getId() != null && existingObj == null)
            existingObj = getByPath(object.getJcrPath());
            

        if (existingObj == null)
        {
            // New
            Assert.notNull(object.getJcrPath(), "jcrPath must not be null when saving.");
            jcrMappingTemplate.insert(object);
            jcrMappingTemplate.save();
            return object;
        }
        else
        {
            // Existing object may not exist if this is an import
            if (!existingObj.getJcrPath().equals(object.getJcrPath()))
                jcrMappingTemplate.move(existingObj.getJcrPath(), object.getJcrPath());
            // Update
            jcrMappingTemplate.update(object);
            jcrMappingTemplate.save();
            return object;
        }
    }

    private boolean canSaveWithRepositoryCheck(T object)
    {
        // check if this is a new node, if so we can safely assume that a save is fine
        if (object.getId() == null)
            return true;

        T compareNode = get(object.getId());

        // Node doesn't exist in repo. Must be an import.
        if (compareNode == null)
            return true;

        // if the changeNumber has changed something else has saved the baseNode while we were working on it. So it shouldn't be saved.
        if (compareNode != null && compareNode.getVersion() == object.getVersion())
            return true;

        // change number is fine so check for the rest 
        if (object.isPublished())
            return true;

        if (SecurityUtil.isCurrentUser(object.getModifier()))
            return true;

        return false;
    }

    public boolean canSave(T object, boolean checkRepository)
    {
        if (checkRepository)
            return canSaveWithRepositoryCheck(object);
        else
            return canSaveNoRepositoryCheck(object);
    }

    private boolean canSaveNoRepositoryCheck(T baseNode)
    {
        // baseNode is published and we haven't been asked to sync with repository
        if (baseNode.isPublished())
            return true;

        // if it is not we can save only if the current AuditInfo.getUserId()  is equal to the current users id
        if (SecurityUtil.isCurrentUser(baseNode.getModifier()))
            return true;

        return false;
    }

    @SuppressWarnings("unchecked")
    public T getByPath(String path)
    {
        Assert.notNull("path must be specified.", path);

        // Removing trailing slash to make path JCR compatible
        if (path.endsWith("/"))
            path = path.substring(0, path.lastIndexOf("/"));

        // FIXME Caching: must be a better place
        if (SecurityUtil.isEditor())
            return (T) jcrMappingTemplate.getObject(path);

        //        return  (T) jcrMappingTemplate.getObject(path);
        Element element = cache.get(path);

        if (element == null)
        {
            T object = (T) jcrMappingTemplate.getObject(path);
            if (object != null && object.isPublished())
            {
                // Only put published objects in the cache
                element = new Element(path, object);
                cache.put(element);
            }
            return object;
        }
        else
        {
            return (T) element.getObjectValue();
        }
    }

    public boolean exists(String id)
    {
        // If there is no id then this object hasn't been saved
        Assert.notNull(id, "id must not be null.");

        // PERF Access node without conversion for faster check
        T entity = get(id);
        return (entity != null);
    }

    public void renderNodeInfo(String uuid)
    {
        try
        {
            Node node = jcrMappingTemplate.getNodeByUUID(uuid);

            jcrMappingTemplate.execute(new JcrCallback()
            {
                public Object doInJcr(Session session) throws RepositoryException
                {
                    try
                    {
                        session.exportDocumentView("/", new FileOutputStream(new File("/tmp/out")), true, true);
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            //            exportDocumentView(String absPath, 
            //                    ContentHandler contentHandler, 
            //                    boolean skipBinary, 
            //                    boolean noRecurse) 

            renderNodeInfo(node, 0);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void renderNodeInfo(Node node, int depth) throws RepositoryException
    {
        if (node.getPath().equals("/jcr:system"))
            return;

        System.err.print("" + node.getPath() + " (" + node.getPrimaryNodeType().getName() + ")" + " [");

        PropertyIterator properties = node.getProperties();
        while (properties.hasNext())
        {
            javax.jcr.Property prop = properties.nextProperty();
            if (prop.getDefinition().isMultiple())
                System.err.print("" + prop.getName() + "=" + prop.getValues() + ", ");
            else
                System.err.print("" + prop.getName() + "=" + prop.getValue().getString() + ", ");
        }
        System.err.println("]");

        NodeIterator childNodes = node.getNodes();
        while (childNodes.hasNext())
        {
            renderNodeInfo(childNodes.nextNode(), depth + 1);
        }
    }

    public boolean existsAtPath(String path)
    {
        // If there in no path then the object hasn't been set up correctly
        Assert.notNull(path, "jcrPath must not be null.");

        // PERF Access node without conversion for faster check
        T entity = getByPath(path);
        return (entity != null);
    }

    @SuppressWarnings("unchecked")
    public T get(final String id)
    {
        try
        {
            return (T) jcrMappingTemplate.execute(new JcrMappingCallback()
            {
                public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
                {

                    Object o = manager.getObjectByUuid(id);

                    return o;
                }
            }, true);
        }
        catch (RuntimeException e)
        {
            return null;
        }
    }

    public void remove(final String id)
    {
        String path = convertIdToPath(id);
        jcrMappingTemplate.remove(path);
        // FIXME Do we need these explicit saves? They break transcations?
        jcrMappingTemplate.save();

        // Delete in live workspace too
        jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    Session liveSession = null;
                    try
                    {
                        // get a live workspace session
                        liveSession = sessionFactory.getSession(OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME);
                        try
                        {
                            Node liveNode = liveSession.getNodeByUUID(id);
                            liveNode.remove();
                            liveSession.save();
                        }
                        catch (Exception e)
                        {
                        }
                    }
                    finally
                    {
                        if (liveSession != null)
                            liveSession.logout();
                    }
                    return null;
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        }, true);
    }

    @SuppressWarnings("unchecked")
    public T copy(final String id, final String newPath)
    {
        return (T) jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    Session session = manager.getSession();
                    Node srcNode = session.getNodeByUUID(id);
                    session.getWorkspace().copy(srcNode.getPath(), newPath);
                    // FIXME Return new node: Node node = session.getRootNode().getNode(newPath);
                    session.save();
                    return null;
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        }, true);
    }

    public T rename(T object, String newPath)
    {
        jcrMappingTemplate.move(object.getJcrPath(), newPath);
        jcrMappingTemplate.save();
        return null; //FIXME Return correct amount.
    }

    @SuppressWarnings("unchecked")
    public T reorder(final T object, final T targetObject, final String point)
    {
        return (T) jcrMappingTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node item = session.getNodeByUUID(object.getId());
                Node target = session.getNodeByUUID(targetObject.getId());

                Assert.doesNotContain(target.getPath(), ".", "Target must be a folder: " + target.getPath());

                /*
                 * Five situations:
                 * 
                 * 1. Simple append
                 * 2. Move above same folder
                 * 3. Move below same folder
                 * 4. Move above different folder
                 * 5. Move below different folder
                 */
                boolean sameFolder = false;
                if (item.getParent().getPath().equals(target.getParent().getPath()))
                    sameFolder = true;

                if (point.equals(GenericJcrDao.REORDER_APPEND) || !sameFolder)
                {
                    String origPath = item.getPath();

                    String newPath;
                    if (point.equals(GenericJcrDao.REORDER_APPEND))
                        // Case 1
                        newPath = target.getPath() + "/" + item.getName();
                    else
                        // Case 4,5
                        newPath = target.getParent().getPath() + "/" + item.getName();

                    GenericJcrDaoJackrabbit.this.logger.info("Moving: " + origPath + " to " + newPath);
                    session.move(origPath, newPath);
                }
                if (point.equals(GenericJcrDao.REORDER_ABOVE))
                {
                    // Case 2, 4
                    item.getParent().orderBefore(item.getName(), target.getName());
                }
                else if (point.equals(GenericJcrDao.REORDER_BELOW))
                {
                    // Case 3, 5
                    NodeIterator nodes = target.getParent().getNodes();
                    boolean found = false;
                    while (nodes.hasNext())
                    {
                        Node n = nodes.nextNode();
                        if (n.isSame(target))
                        {
                            found = true;
                            break;
                        }
                    }
                    Assert.isTrue(found, "Target node not found.");
                    if (nodes.hasNext())
                        item.getParent().orderBefore(item.getName(), nodes.nextNode().getName());
                    else
                        item.getParent().orderBefore(item.getName(), null);
                }

                session.save();
                return null;
            }
        });
    }

    /**
     * Looks up node by UUID and then gets node path.
     * 
     * <p>
     * PERF This is required until the OCM persistence manager supports actions
     * by UUID not just paths.
     * 
     * @param uuid
     * @return
     */
    private String convertIdToPath(String uuid)
    {
        try
        {
            return jcrMappingTemplate.getNodeByUUID(uuid).getPath();
        }
        catch (RepositoryException e)
        {
            throw new ObjectRetrievalFailureException(this.persistentClass, uuid);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getVersions(final T object)
    {
        return (List<T>) jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    List<T> list = new ArrayList<T>();

                    // get underlying node
                    Node node = manager.getSession().getNodeByUUID(object.getId());

                    VersionIterator versions = node.getVersionHistory().getAllVersions();
                    while (versions.hasNext())
                    {
                        Version version = versions.nextVersion();
                        if (!version.getName().equals("jcr:rootVersion")) //  don't include the root version as it can't be object mapped
                        {
                            Object versionObject = manager.getObject(object.getJcrPath(), version.getName());
                            list.add((T) versionObject);
                        }
                    }
                    return list;
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        }, true);
    }

    public void publish(final T baseNode, final String message)
    {
        //FIXME this should display proper transactional behaviour which it doesn't at the moment as there are multiple jcr sessions involved
        //        if (baseNode.isPublished())
        //            throw new OtherObjectsException("baseNode " + baseNode.getJcrPath() + "[" + baseNode.getId() + "] couldn't be published as its published flag is already set ");

        if (this.cache != null)
            this.cache.put(new Element(baseNode.getJcrPath(), baseNode));

        // run node through rule engine if it is a CmsNode and cancel publish if rule engine doesn't set publish flag
        if (baseNode instanceof CmsNode)
        {
            //            CmsNode nodeToInsertIntoRuleEngine = baseNode;
            //            Object[] result = ruleExecutor.runInStatelessSession(new Object[]{nodeToInsertIntoRuleEngine}, Boolean.class);
            //
            //            if (!(result.length > 0) || !(result[0] instanceof Boolean) || !((Boolean) result[0]).booleanValue())
            //                return;
        }

        jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    Session liveSession = null;
                    String jcrPath = baseNode.getJcrPath();
                    try
                    {
                        // get a live workspace session
                        liveSession = sessionFactory.getSession(OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME);
                        Workspace liveWorkspace = liveSession.getWorkspace();

                        Node liveNode = null;
                        try
                        {
                            //get the corresponding node in the live workspace by UUID in case path has changed
                            liveNode = liveSession.getNodeByUUID(baseNode.getId());
                        }
                        catch (ItemNotFoundException e)
                        {
                            // noop
                        }

                        if (liveNode == null) // no such node so we can clone
                        {
                            liveWorkspace.clone(OtherObjectsJackrabbitSessionFactory.EDIT_WORKSPACE_NAME, jcrPath, jcrPath, false);
                        }
                        else
                        {
                            liveNode.update(OtherObjectsJackrabbitSessionFactory.EDIT_WORKSPACE_NAME);
                            if (!baseNode.getJcrPath().equals(liveNode.getPath()))
                                liveWorkspace.move(liveNode.getPath(), baseNode.getJcrPath());
                        }
                    }
                    finally
                    {
                        if (liveSession != null)
                            liveSession.logout();
                    }
                    return null;
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        }, true);

        // Update edited node. Must be done in separate transaction. See: 
        jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                String jcrPath = baseNode.getJcrPath();
                try
                {
                    // we got here so we successfully published
                    updateAuditInfo(baseNode, message);
                    saveInternal(baseNode, true); // set the status to published

                    // create version and assign the current changeNumber as the label
                    baseNode.setVersion(baseNode.getVersion() + 1);
                    manager.checkin(jcrPath, new String[]{(baseNode.getVersion()) + ""});
                    manager.checkout(jcrPath);

                    applicationContext.publishEvent(new PublishEvent(this, baseNode));
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
                return null;
            }
        }, true);

    }

    private void updateAuditInfo(T baseNode, String comment)
    {
        // FIXME Need to deal with creation data
        User user = SecurityUtil.getCurrentUser();
        if (user != null) // FIXME Need to reenable and then mock in tests
        {
            if (baseNode.getCreator() == null)
                baseNode.setCreator(user.getUsername());

            Assert.notNull(user, "auditInfo can't be updated if there is no current user");
            baseNode.setModifier(user.getUsername());
        }
        baseNode.setModificationTimestamp(new Date());
        if (baseNode.getCreationTimestamp() == null)
            baseNode.setCreationTimestamp(new Date());
        if (StringUtils.isNotEmpty(comment))
            baseNode.setEditingComment(comment);
        baseNode.setVersion(baseNode.getVersion() + 1);
    }

    @SuppressWarnings("unchecked")
    public T getVersionByChangeNumber(final T object, final int changeNumber)
    {
        return (T) jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    String versionName = getVersionNameFromLabel(object.getId(), changeNumber + 1 + "", manager.getSession());
                    return manager.getObject(object.getJcrPath(), versionName);
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        }, true);
    }

    @SuppressWarnings("unchecked")
    public T restoreVersionByChangeNumber(final T object, final int changeNumber)
    {
        return (T) jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    //we need to fall back into native jcr as ObjectContentManager hasn't got a restore equivalent yet
                    Session session = manager.getSession();
                    Node node = session.getNodeByUUID(object.getId());
                    printVersionHistory(node.getVersionHistory());

                    // FIXME We need to increment the version by 1? Something iffy here 
                    String requestedVersionName = node.getVersionHistory().getVersionByLabel(changeNumber + 1 + "").getName();
                    Version requestedVersion = node.getVersionHistory().getVersion(requestedVersionName);
                    node.restore(requestedVersion, false);

                    // Checkout so changes can be made again
                    node.checkout();

                    // Update change number
                    int newChangeNumber = object.getVersion() + 1;
                    node.setProperty("changeNumber", newChangeNumber);
                    node.save();

                    // re-get object
                    return manager.getObjectByUuid(object.getId());
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        }, true);
    }

    public String getVersionNameFromLabel(String uuid, String label, Session session) throws RepositoryException
    {
        Node node = session.getNodeByUUID(uuid); // get Node
        Version requestedVersion = node.getVersionHistory().getVersionByLabel(label);
        return requestedVersion.getName();
    }

    private void printVersionHistory(VersionHistory versionHistory) throws RepositoryException
    {
        for (VersionIterator vi = versionHistory.getAllVersions(); vi.hasNext();)
        {
            Version version = vi.nextVersion();
            if (!version.getName().equals("jcr:rootVersion"))
                System.err.println("label(s):" + versionHistory.getVersionLabels(version)[0] + " name: " + version.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll()
    {
        QueryManager queryManager = getJcrMappingTemplate().createQueryManager();
        Filter filter = queryManager.createFilter(persistentClass);
        Query query = queryManager.createQuery(filter);
        return (List<T>) getJcrMappingTemplate().getObjects(query);
    }

    /**
     * Works in transactions.
     */
    @SuppressWarnings("unchecked")
    public List<T> getAllByPath(final String path)
    {
        return (List<T>) jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    String p = path;
                    if (path.length() > 1 && path.endsWith("/"))
                        p = path.substring(0, path.length() - 1);

                    List<T> list = new ArrayList<T>();
                    Node node;
                    if (p.equals("/"))
                        node = manager.getSession().getRootNode();
                    else
                        node = manager.getSession().getRootNode().getNode(p.substring(1));

                    NodeIterator nodes = node.getNodes();
                    while (nodes.hasNext())
                    {
                        Node n = nodes.nextNode();
                        //FIXME Extra lookup is bad. Can we avoid UUID requirement too
                        //FIXME Avoid jcr: nodes better...
                        if (!n.getName().startsWith("jcr:") && n.getPrimaryNodeType().getName().equals("oo:node"))
                            list.add((T) manager.getObjectByUuid(n.getUUID()));
                    }
                    return list;
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        }, true);

        //        QueryManager queryManager = jcrMappingTemplate.createQueryManager();
        //        Filter filter = queryManager.createFilter(baseNode.class);
        //        Query query = queryManager.createQuery(filter);
        //        filter.setScope(path + "/");
        //        return (List<T>) jcrMappingTemplate.getObjects(query);
    }

    @SuppressWarnings("unchecked")
    public T getByJcrExpression(final String jcrExpression)
    {
        return (T) jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    javax.jcr.query.QueryManager queryManager = manager.getSession().getWorkspace().getQueryManager();
                    javax.jcr.query.Query query = queryManager.createQuery(jcrExpression, javax.jcr.query.Query.XPATH);
                    javax.jcr.query.QueryResult queryResult = query.execute();
                    NodeIterator nodeIterator = queryResult.getNodes();
                    //FIXME This is a double lookup. Can we convert node directly?
                    return manager.getObjectByUuid(nodeIterator.nextNode().getUUID());
                }
                catch (NoSuchElementException e)
                {
                    // No matching node found
                    return null;
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<T> getAllByJcrExpression(final String jcrExpression)
    {
        return (List<T>) jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    String selector = null;
                    String jcr = jcrExpression;
                    if (jcrExpression.contains("{"))
                    {
                        int selectorStart = jcrExpression.lastIndexOf("{");
                        jcr = jcr.substring(0, selectorStart);
                        selector = jcrExpression.substring(selectorStart);
                    }

                    javax.jcr.query.QueryManager queryManager = manager.getSession().getWorkspace().getQueryManager();
                    javax.jcr.query.Query query = queryManager.createQuery(jcr, javax.jcr.query.Query.XPATH);
                    javax.jcr.query.QueryResult queryResult = query.execute();
                    NodeIterator nodeIterator = queryResult.getNodes();
                    List<T> results = new ArrayList<T>();
                    //FIXME This is a double lookup. Can we convert node directly?
                    while (nodeIterator.hasNext())
                    {
                        Node nextNode = nodeIterator.nextNode();
                        try
                        {
                            results.add((T) manager.getObjectByUuid(nextNode.getUUID()));
                        }
                        catch (Exception e)
                        {
                            logger.warn("Could not export: " + nextNode.getPath());
                        }
                    }

                    if (selector != null)
                        return new RangeSelector<T>(selector, results).getSelected();
                    else
                        return results;
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public PagedList<T> pageByJcrExpression(final String jcrExpression, final int pageSize, final int pageNo)
    {
        return (PagedList<T>) jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    javax.jcr.query.QueryManager queryManager = manager.getSession().getWorkspace().getQueryManager();
                    javax.jcr.query.Query query = queryManager.createQuery(jcrExpression, javax.jcr.query.Query.XPATH);
                    javax.jcr.query.QueryResult queryResult = query.execute();
                    return createPagedResults(manager, queryResult, pageSize, pageNo);
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public PagedList<T> getPagedByPath(final String path, final int pageSize, final int pageNo)
    {
        return (PagedList<T>) jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    String p = path;
                    if (path.length() > 1 && path.endsWith("/"))
                        p = path.substring(0, path.length() - 1); // cut of trailing slash

                    javax.jcr.query.QueryManager queryManager = manager.getSession().getWorkspace().getQueryManager();
                    javax.jcr.query.Query query = queryManager.createQuery("/jcr:root" + p + "/element(*, oo:node)", javax.jcr.query.Query.XPATH);

                    javax.jcr.query.QueryResult queryResult = query.execute();
                    // first count results
                    NodeIterator nodeIterator = queryResult.getNodes();
                    long count = nodeIterator.getSize();

                    int startIndex = PagedListImpl.calcStartIndex(pageSize, pageNo);
                    int endIndex = PagedListImpl.calcEndIndex(pageSize, (int) count, startIndex); //FIXME we are downcasting to int here which could theoretically cause problems ...

                    // now do a loop and store the range of interest in a list
                    List<T> nodes = new ArrayList<T>();
                    int i = 0;
                    while (nodeIterator.hasNext())
                    {
                        if (i >= startIndex && i < endIndex)
                            nodes.add((T) manager.getObjectByUuid(nodeIterator.nextNode().getUUID()));
                        else
                            nodeIterator.nextNode();

                        if (i >= endIndex)
                            break;
                        i++;
                    }

                    return new PagedListImpl<T>(pageSize, (int) count, pageNo, nodes, false);
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        }, true);
    }

    @SuppressWarnings("unchecked")
    public PagedList<T> getPagedByPath(final String path, final int pageSize, final int pageNo, final String search, final String sortField, final boolean asc)
    {
        return (PagedList<T>) jcrMappingTemplate.execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    /* a jcr xpath query looks like this :
                     * 
                     * /jcr:root/path//element(*, oo:node) [@attr1 = 'abc' and @attr2 = 'def' and jcr:contains(., 'some text')] order by @attr3 ascending
                     * |        |     |                   |                                  |    |                           | |                        |
                     *  rep root        what kind of nodes            where clause                      text search                    order by clause
                     */

                    // basic xpath
                    StringBuffer queryString = new StringBuffer();
                    queryString.append("/jcr:root");
                    queryString.append(path);
                    queryString.append("/");
                    queryString.append("element(*, oo:node)");
                    queryString.append(" ");

                    StringBuffer searchString = new StringBuffer();

                    // full text search
                    boolean isSearch = false;
                    if (StringUtils.isNotBlank(search))
                    {
                        isSearch = true;
                        searchString.append("jcr:contains(.,'");
                        searchString.append(search);
                        searchString.append("')");
                    }

                    // make sure folders are not included in search results
                    //FIXME this is a temporary hack and needs to be superseded by something better
                    queryString.append("[@ooType!='org.otherobjects.cms.model.SiteFolder'");
                    if (isSearch)
                        queryString.append(" and ").append(searchString.toString());
                    queryString.append("]");

                    // ordering
                    if (StringUtils.isNotBlank(sortField))
                    {
                        queryString.append(" order by ");
                        queryString.append("@");
                        queryString.append(sortField);
                        queryString.append(" ");
                        if (asc)
                            queryString.append("ascending");
                        else
                            queryString.append("descending");

                    }
                    else if (isSearch) // no specific order colum spec. but we are im search so we might as well order by relevance
                    {
                        queryString.append(" order by jcr:score() descending");
                    }

                    javax.jcr.query.QueryManager queryManager = manager.getSession().getWorkspace().getQueryManager();
                    javax.jcr.query.Query query = queryManager.createQuery(queryString.toString(), javax.jcr.query.Query.XPATH);

                    javax.jcr.query.QueryResult queryResult = query.execute();
                    return createPagedResults(manager, queryResult, pageSize, pageNo);
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }

        }, true);
    }

    @SuppressWarnings("unchecked")
    private Object createPagedResults(ObjectContentManager manager, javax.jcr.query.QueryResult queryResult, int pageSize, int pageNo) throws RepositoryException
    {
        // first count results
        NodeIterator nodeIterator = queryResult.getNodes();
        long count = nodeIterator.getSize();

        int startIndex = PagedListImpl.calcStartIndex(pageSize, pageNo);
        int endIndex = PagedListImpl.calcEndIndex(pageSize, (int) count, startIndex); //FIXME we are downcasting to int here which could theoretically cause problems ...

        // now do a loop and store the range of interest in a list
        List<T> nodes = new ArrayList<T>();
        int i = 0;
        while (nodeIterator.hasNext())
        {
            if (i >= startIndex && i < endIndex)
                nodes.add((T) manager.getObjectByUuid(nodeIterator.nextNode().getUUID()));
            else
                nodeIterator.nextNode();

            if (i >= endIndex)
                break;
            i++;
        }

        return new PagedListImpl<T>(pageSize, (int) count, pageNo, nodes, false);
    }

    public PagedList<T> getAllPaged(int pageSize, int pageNo, String filterQuery, String sortField, boolean asc)
    {
        //FIXME needs to be implemented
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public PagedList<T> getPagedByQuery(String query, int pageSize, int pageNo, String filterQuery, String sortField, boolean asc)
    {
        //FIXME needs to be implemented
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @category injector
     */
    public void setJcrMappingTemplate(JcrMappingTemplate jcrMappingTemplate)
    {
        this.jcrMappingTemplate = jcrMappingTemplate;
    }

    /**
     * @category injector
     */
    public void setSessionFactory(OtherObjectsJackrabbitSessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    /**
     * @category injector
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public JcrMappingTemplate getJcrMappingTemplate()
    {
        return jcrMappingTemplate;
    }

    public void setValidator(Validator validator)
    {
        this.validator = validator;
    }

    public T create()
    {
        try
        {
            return persistentClass.newInstance();
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Unable to create new class of type: " + persistentClass, e);
        }
    }

    //    public void setRuleExecutor(RuleExecutor ruleExecutor)
    //    {
    //        this.ruleExecutor = ruleExecutor;
    //    }
}
