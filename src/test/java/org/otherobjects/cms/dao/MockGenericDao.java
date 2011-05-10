package org.otherobjects.cms.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MockGenericDao implements GenericDao<Serializable, Serializable>
{
    private Serializable mockObject;
    private List<Object> allObjects;
    private Map<Serializable, Serializable> keyedObjects = Collections.EMPTY_MAP;

    public MockGenericDao()
    {
        allObjects = new ArrayList<Object>();
    }
    

    public Class<Serializable> getPersistentClass()
    {
        return (Class<Serializable>) mockObject.getClass();
    }
    
    public String getPersistentClassName()
    {
        // Try if possible
        return mockObject.getClass().getName();
    }
    
    public MockGenericDao(Serializable mockObject)
    {
        this.mockObject = mockObject;
        allObjects = new ArrayList<Object>();
        allObjects.add(mockObject);
    }

    public MockGenericDao(List<Object> mockObjects)
    {
        this.allObjects = mockObjects;
    }

    public MockGenericDao(Map mockObjects)
    {
        this.keyedObjects = mockObjects;
    }

    public boolean exists(Serializable id)
    {
        return this.mockObject != null;
    }

    public Serializable get(Serializable id)
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
        return new PagedListImpl<Object>(pageSize, pageNo, allObjects);
    }

    public PagedList getPagedByQuery(String query, int pageSize, int pageNo, String filterQuery, String sortField, boolean asc)
    {
        return new PagedListImpl<Object>(pageSize, pageNo, allObjects);
    }

    public void remove(Serializable id)
    {

    }

    public Serializable save(Serializable object)
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

    public Serializable save(Serializable object, boolean validate)
    {
        return save(object);
    }

    public Serializable create()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
