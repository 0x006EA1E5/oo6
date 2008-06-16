package org.otherobjects.cms.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MockGenericDao implements GenericDao
{
    private Object mockObject;
    private List<Object> allObjects;
    private Map<String, Object> keyedObjects;

    public MockGenericDao(Object mockObject)
    {
        this.mockObject = mockObject;
        allObjects = new ArrayList<Object>();
        allObjects.add(mockObject);
    }

    public MockGenericDao(List<Object> mockObjects)
    {
        this.allObjects = mockObjects;
    }

    public MockGenericDao(Map<String, Object> mockObjects)
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
        return new PagedListImpl<Object>(pageSize, pageNo, allObjects);
    }

    public PagedList getPagedByQuery(String query, int pageSize, int pageNo, String filterQuery, String sortField, boolean asc)
    {
        return new PagedListImpl<Object>(pageSize, pageNo, allObjects);
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

}
