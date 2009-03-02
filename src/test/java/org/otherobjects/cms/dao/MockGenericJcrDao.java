package org.otherobjects.cms.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.otherobjects.cms.model.CmsNode;

@SuppressWarnings("unchecked")
public class MockGenericJcrDao implements GenericJcrDao
{
    private CmsNode mockObject;
    private List<CmsNode> allObjects;
    private Map<Serializable, CmsNode> keyedObjects = Collections.EMPTY_MAP;

    public MockGenericJcrDao()
    {
        allObjects = new ArrayList<CmsNode>();
    }
    
    public String getPersistentClassName()
    {
        // Try if possible
        return mockObject.getClass().getName();
    }
    
    public MockGenericJcrDao(CmsNode mockObject)
    {
        this.mockObject = mockObject;
        allObjects = new ArrayList<CmsNode>();
        allObjects.add(mockObject);
    }

    public MockGenericJcrDao(List<CmsNode> mockObjects)
    {
        this.allObjects = mockObjects;
    }

    public MockGenericJcrDao(Map<Serializable, CmsNode> mockObjects)
    {
        this.keyedObjects = mockObjects;
    }

    public boolean exists(Serializable id)
    {
        return this.mockObject != null;
    }

    public Object get(Serializable id)
    {
        if (keyedObjects.containsKey(id))
            return keyedObjects.get(id);
        else
            return this.mockObject;
    }

    public List getAll()
    {
        return allObjects;
    }

    public PagedList getAllPaged(int pageSize, int pageNo, String filterQuery, String sortField, boolean asc)
    {
        return new PagedListImpl<CmsNode>(pageSize, pageNo, allObjects);
    }

    public PagedList getPagedByQuery(String query, int pageSize, int pageNo, String filterQuery, String sortField, boolean asc)
    {
        return new PagedListImpl<CmsNode>(pageSize, pageNo, allObjects);
    }

    public void remove(Serializable id)
    {

    }

    public Object save(Object object)
    {
        try
        {
            Method idSetter = object.getClass().getMethod("setId", new Class[]{String.class});
            if (idSetter != null)
            {
                idSetter.invoke(object, new Object[]{new String("f81d4fae-7dec-11d0-a765-00a0c91e6bf6")});
            }
        }
        catch (Exception e)
        {
            //noop
        }

        return object;
    }

    public Object save(Object object, boolean validate)
    {
        return save(object);
    }

    public Object create()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean existsAtPath(String path)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public List getAllByJcrExpression(String expression)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getAllByPath(String path)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getByJcrExpression(String expression)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getByPath(String path)
    {
        for(CmsNode o : allObjects)
        {
            if(o.getJcrPath() != null && o.getJcrPath().equals(path))
                return o;
        }
        return null;
    }

    public PagedList getPagedByPath(String path, int pageSize, int pageNo)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PagedList getPagedByPath(String path, int pageSize, int pageNo, String search, String sortField, boolean asc)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getVersionByChangeNumber(Object object, int changeNumber)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getVersions(Object object)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PagedList pageByJcrExpression(String expression, int pageSize, int pageNo)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void publish(Object object, String message)
    {
        // TODO Auto-generated method stub
        
    }

    public Object rename(Object node, String path)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void renderNodeInfo(String id)
    {
        // TODO Auto-generated method stub
        
    }

    public Object reorder(Object object, Object target, String position)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Object restoreVersionByChangeNumber(Object object, int changeNumber)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
