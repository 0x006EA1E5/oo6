package org.otherobjects.cms.jcr;

import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.jackrabbit.ocm.spring.JcrMappingTemplate;
import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.DynaNode;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.util.Assert;

/**
 * Base class for all JCR DAOs.
 * 
 * @author rich
 */
public class GenericJcrDaoJackrabbit<T extends CmsNode> implements GenericJcrDao<T>
{
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
    public List<T> getAllByPath(String path)
    {
        QueryManager queryManager = getJcrMappingTemplate().createQueryManager();
        Filter filter = queryManager.createFilter(DynaNode.class);
        Query query = queryManager.createQuery(filter);
        filter.setScope(path + "/");
        return (List<T>) getJcrMappingTemplate().getObjects(query);
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
