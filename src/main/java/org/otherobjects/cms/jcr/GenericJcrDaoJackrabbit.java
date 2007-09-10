package org.otherobjects.cms.jcr;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.ocm.exception.JcrMappingException;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.jackrabbit.ocm.spring.JcrMappingCallback;
import org.apache.jackrabbit.ocm.spring.JcrMappingTemplate;
import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.dao.PagedResult;
import org.otherobjects.cms.dao.PagedResultImpl;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.DynaNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.util.Assert;
import org.springmodules.jcr.JcrCallback;

/**
 * Base class for all JCR DAOs.
 * 
 * @author rich
 */
public class GenericJcrDaoJackrabbit<T extends CmsNode> implements GenericJcrDao<T>
{
    private final Logger logger = LoggerFactory.getLogger(GenericJcrDaoJackrabbit.class);

    private final Class<T> persistentClass;

    protected JcrMappingTemplate jcrMappingTemplate;

    public GenericJcrDaoJackrabbit(Class<T> persistentClass)
    {
        this.persistentClass = persistentClass;
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll()
    {
        QueryManager queryManager = getJcrMappingTemplate().createQueryManager();
        Filter filter = queryManager.createFilter(this.persistentClass);
        Query query = queryManager.createQuery(filter);
        return (List<T>) getJcrMappingTemplate().getObjects(query);
    }

    /**
     * Works in transactions.
     * 
     */
    @SuppressWarnings("unchecked")
    public List<T> getAllByPath(final String path)
    {
        return (List<T>) getJcrMappingTemplate().execute(new JcrMappingCallback()
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
                        if (!n.getName().startsWith("jcr:"))
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

        //        QueryManager queryManager = getJcrMappingTemplate().createQueryManager();
        //        Filter filter = queryManager.createFilter(DynaNode.class);
        //        Query query = queryManager.createQuery(filter);
        //        filter.setScope(path + "/");
        //        return (List<T>) getJcrMappingTemplate().getObjects(query);
    }

    @SuppressWarnings("unchecked")
    public T getByJcrExpression(final String jcrExpression)
    {
        return (T) getJcrMappingTemplate().execute(new JcrMappingCallback()
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
        return (List<T>) getJcrMappingTemplate().execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    javax.jcr.query.QueryManager queryManager = manager.getSession().getWorkspace().getQueryManager();
                    javax.jcr.query.Query query = queryManager.createQuery(jcrExpression, javax.jcr.query.Query.XPATH);
                    javax.jcr.query.QueryResult queryResult = query.execute();
                    NodeIterator nodeIterator = queryResult.getNodes();
                    List<T> results = new ArrayList<T>();
                    //FIXME This is a double lookup. Can we convert node directly?
                    int count = 0;
                    while (nodeIterator.hasNext() && count++ < 10)
                        results.add((T) manager.getObjectByUuid(nodeIterator.nextNode().getUUID()));

                    return results;
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        });
    }

    /**
     * Saves the object in the repository. If the object already exists then it
     * is updated, otherwise it is inserted.
     */
    @SuppressWarnings("unchecked")
    public T save(T object)
    {
        return save(object, true);
    }

    public T save(T object, boolean validate)
    {
        if (object.getId() == null)
        {
            // New
            Assert.notNull(object.getJcrPath(), "jcrPath must not be null when saving.");
            getJcrMappingTemplate().insert(object);

            // PERF Extra lookup required to get UUID. Should be done in PM.
            CmsNode newObj = getByPath(object.getJcrPath());
            Assert.notNull(newObj, "Object not saved correctly. Could not read ID.");
            object.setId(newObj.getId());
            getJcrMappingTemplate().save();
            return object;
        }
        else
        {
            // PERF Extra lookup required to check path change
            CmsNode existingObj = get(object.getId());
            if (!existingObj.getJcrPath().equals(object.getJcrPath()))
                getJcrMappingTemplate().move(existingObj.getJcrPath(), object.getJcrPath());

            // Update
            getJcrMappingTemplate().update(object);
            getJcrMappingTemplate().save();
            return object;
        }
    }

    /**
     * Moves the object to a new location in the repository. This is also used for 
     * renaming nodes.
     * 
     * <p>FIXME Can we detect path changes automatically?
     */
    @SuppressWarnings("unchecked")
    public T move(T object, String newPath)
    {
        getJcrMappingTemplate().move(object.getJcrPath(), newPath);
        getJcrMappingTemplate().save();
        return null; //FIXME
    }

    public boolean existsAtPath(String path)
    {
        // If there in no path then the object hasn't been set up correctly
        Assert.notNull(path, "jcrPath must not be null.");

        // PERF Access node without conversion for faster check
        T entity = getByPath(path);
        if (entity != null)
            return true;
        else
            return false;
    }

    @SuppressWarnings("unchecked")
    public T getByPath(String path)
    {
        Assert.notNull("path must be specified.", path);

        // Removing trainling slash to make path JCR compatible
        if (path.endsWith("/"))
            path = path.substring(0, path.lastIndexOf("/"));

        return (T) getJcrMappingTemplate().getObject(path);
    }

    public boolean exists(String id)
    {
        // If there in no id then this object hasn't been saved
        if (id == null)
            return false;

        // PERF Access node without conversion for faster check
        T entity = get(id);
        if (entity != null)
            return true;
        else
            return false;
    }

    @SuppressWarnings("unchecked")
    public T get(String id)
    {
        try
        {
            return (T) getJcrMappingTemplate().getObjectByUuid(id);
        }
        catch (RuntimeException e)
        {
            return null;
        }
    }

    public void remove(String id)
    {
        String path = convertIdToPath(id);
        getJcrMappingTemplate().remove(path);
    }

    public void moveItem(final String itemId, final String targetId, final String point)
    {
        getJcrMappingTemplate().execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node item = session.getNodeByUUID(itemId);
                Node target = session.getNodeByUUID(targetId);

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

                if (point.equals("append") || !sameFolder)
                {
                    String origPath = item.getPath();

                    String newPath;
                    if (point.equals("append"))
                        // Case 1
                        newPath = target.getPath() + "/" + item.getName();
                    else
                        // Case 4,5
                        newPath = target.getParent().getPath() + "/" + item.getName();

                    GenericJcrDaoJackrabbit.this.logger.info("Moving: " + origPath + " to " + newPath);
                    session.move(origPath, newPath);
                }
                if (point.equals("above"))
                {
                    // Case 2, 4
                    item.getParent().orderBefore(item.getName(), target.getName());
                }
                else if (point.equals("below"))
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
     * PERF This is required until the OCM presistence manager supports actions
     * by UUID not just paths.
     * 
     * @param uuid
     * @return
     */
    private String convertIdToPath(String uuid)
    {
        try
        {
            return getJcrMappingTemplate().getNodeByUUID(uuid).getPath();
        }
        catch (RepositoryException e)
        {
            throw new ObjectRetrievalFailureException(this.persistentClass, uuid);
        }
    }

    public JcrMappingTemplate getJcrMappingTemplate()
    {
        return this.jcrMappingTemplate;
    }

    public void setJcrMappingTemplate(JcrMappingTemplate jcrMappingTemplate)
    {
        this.jcrMappingTemplate = jcrMappingTemplate;
    }

    @SuppressWarnings("unchecked")
    public List<T> getVersions(final T object)
    {
        return (List<T>) getJcrMappingTemplate().execute(new JcrMappingCallback()
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

    @SuppressWarnings("unchecked")
    public T getVersionByChangeNumber(final T object, final int changeNumber)
    {
        return (T) getJcrMappingTemplate().execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    String versionName = getVersionNameFromLabel(object.getId(), changeNumber + "", manager.getSession());
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
    public T restoreVersionByChangeNumber(final T object, final int changeNumber, final boolean removeExisting)
    {
        return (T) getJcrMappingTemplate().execute(new JcrMappingCallback()
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
                    node.restore(requestedVersion, removeExisting);

                    // Checkout so changes can be made again
                    node.checkout();

                    // Update change number
                    int newChangeNumber = ((DynaNode) object).getChangeNumber() + 1;
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
    public PagedResult<T> getPagedByPath(final String path, final int pageSize, final int pageNo)
    {
        return (PagedResult<T>) getJcrMappingTemplate().execute(new JcrMappingCallback()
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

                    int startIndex = PagedResultImpl.calcStartIndex(pageSize, pageNo);
                    int endIndex = PagedResultImpl.calcEndIndex(pageSize, (int) count, startIndex); //FIXME we are downcasting to int here which could theoretically cause problems ...

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

                    return new PagedResultImpl<T>(pageSize, (int) count, pageNo, nodes, false);
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        }, true);
    }

    @SuppressWarnings("unchecked")
    public PagedResult<T> getPagedByPath(final String path, final int pageSize, final int pageNo, final String search, final String sortField, final boolean asc)
    {
        return (PagedResult<T>) getJcrMappingTemplate().execute(new JcrMappingCallback()
        {
            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
            {
                try
                {
                    /* a jcr xpath query looks like this :
                     * 
                     * /jcr:root/path//element(*, oo:node) [@attr1 = 'abc' and @attr2 = 'def' and jcr:contains(., 'some text')] order by @attr3 ascending
                     * |        |     |                   |                                  |    |                           | |                        |
                     *	rep root        what kind of nodes            where clause                      text search                    order by clause
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
                    // first count results
                    NodeIterator nodeIterator = queryResult.getNodes();
                    long count = nodeIterator.getSize();

                    int startIndex = PagedResultImpl.calcStartIndex(pageSize, pageNo);
                    int endIndex = PagedResultImpl.calcEndIndex(pageSize, (int) count, startIndex); //FIXME we are downcasting to int here which could theoretically cause problems ...

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

                    return new PagedResultImpl<T>(pageSize, (int) count, pageNo, nodes, false);
                }
                catch (Exception e)
                {
                    throw new JcrMappingException(e);
                }
            }
        }, true);
    }

    public PagedResult<T> getAllPaged(int pageSize, int pageNo, String filterQuery, String sortField, boolean asc)
    {
        //FIXME needs to be implemented
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public PagedResult<T> getPagedByQuery(String query, int pageSize, int pageNo, String filterQuery, String sortField, boolean asc)
    {
        //FIXME needs to be implemented
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //    @SuppressWarnings("unchecked")
    //	public PagedResult<T> getPagedByPath(final DynaNode dynaNode, final int pageSize, final int pageNo, final String sortField, final boolean asc) {
    //		return (PagedResult<T>) getJcrMappingTemplate().execute(new JcrMappingCallback()
    //        {
    //            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
    //            {
    //                try
    //                {
    //                    String p = dynaNode.getJcrPath();
    //                    Assert.isTrue(!p.endsWith("/"), "jcr paths obtained from  dynaNodes must not end in a slash");
    //                    p = p + "/";
    //                    
    //                    //Use the jackrabbit-ocm infrastructure to build the jcr query
    //                    QueryManager queryManager = getJcrMappingTemplate().createQueryManager();
    //                    Filter filter = queryManager.createFilter(Class.forName(dynaNode.getOoType()));
    //                    
    //                    filter.setScope(p);
    //                    
    //                    Query query = queryManager.createQuery(filter);
    //                    if(sortField != null)
    //                    {
    //                    	if(asc)
    //                    		query.addOrderByAscending(sortField);
    //                    	else
    //                    		query.addOrderByDescending(sortField);
    //                    }
    //                    
    //                    String xPathQuery = queryManager.buildJCRExpression(query);
    //                    logger.debug("getPagedByPath query: " + xPathQuery);
    //                    
    //                    javax.jcr.query.QueryManager jcrQueryManager = manager.getSession().getWorkspace().getQueryManager();
    //                    javax.jcr.query.Query jcrQuery = jcrQueryManager.createQuery(xPathQuery, javax.jcr.query.Query.XPATH);
    //                    
    //                    
    //                    javax.jcr.query.QueryResult queryResult = jcrQuery.execute();
    //                    // first count results
    //                    NodeIterator nodeIterator = queryResult.getNodes();
    //                    long count = nodeIterator.getSize();
    //                    
    //                    int startIndex = PagedResultImpl.calcStartIndex(pageSize, pageNo);
    //                    int endIndex = PagedResultImpl.calcEndIndex(pageSize, (int)count, startIndex); //FIXME we are downcasting to int here which could theoretically cause problems ...
    //                    
    //                    // now do a loop and store the range of interest in a list
    //                    List<T> nodes = new ArrayList<T>();
    //                    int i = 0;
    //                    while(nodeIterator.hasNext())
    //                    {
    //                    	if(i >= startIndex && i < endIndex)
    //                    		nodes.add((T) manager.getObjectByUuid(nodeIterator.nextNode().getUUID()));
    //                    	else
    //                    		nodeIterator.nextNode();
    //                    	
    //                    	if(i >= endIndex)
    //                    		break;
    //                    	i++;
    //                    }
    //                    
    //                    return new PagedResultImpl<T>(pageSize, (int)count, pageNo, nodes, false);
    //                }
    //                catch (Exception e)
    //                {
    //                    throw new JcrMappingException(e);
    //                }
    //            }
    //        }, true);
    //	}
}
