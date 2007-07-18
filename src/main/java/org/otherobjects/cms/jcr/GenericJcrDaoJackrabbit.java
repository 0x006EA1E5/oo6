package org.otherobjects.cms.jcr;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.ocm.exception.JcrMappingException;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.jackrabbit.ocm.spring.JcrMappingCallback;
import org.apache.jackrabbit.ocm.spring.JcrMappingTemplate;
import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.model.CmsNode;
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
    private Logger logger = LoggerFactory.getLogger(GenericJcrDaoJackrabbit.class);

    private Class<T> persistentClass;

    private JcrMappingTemplate jcrMappingTemplate;

    public GenericJcrDaoJackrabbit(Class<T> persistentClass)
    {
        this.persistentClass = persistentClass;
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll()
    {
        QueryManager queryManager = getJcrMappingTemplate().createQueryManager();
        Filter filter = queryManager.createFilter(persistentClass);
        Query query = queryManager.createQuery(filter);
        return (List<T>) getJcrMappingTemplate().getObjects(query);
    }

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
                        //FIXME Extra lookup is bad. Can we avorid UUID requirement too
                        //FIXME Avoid data nodes better...
                        if (!n.getName().equals("data") && !n.getName().startsWith("jcr:") && !n.getName().startsWith("types"))
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
    public List<T> getAllByJcrExperssion(String jcrExpression)
    {
        return null;
    }

    /**
     * Saves the object in the repository. If the object already exists then it
     * is updated, otherwise it is inserted.
     */
    @SuppressWarnings("unchecked")
    public T save(T object)
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

                    logger.info("Moving: " + origPath + " to " + newPath);
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
                        if (n.getUUID().equals(target.getUUID()))
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
        return jcrMappingTemplate;
    }

    public void setJcrMappingTemplate(JcrMappingTemplate jcrMappingTemplate)
    {
        this.jcrMappingTemplate = jcrMappingTemplate;
    }
}
