package org.otherobjects.cms.datastore;

import java.util.List;

import javax.annotation.Resource;

import org.otherobjects.cms.dao.DaoService;

public class JackrabbitDataStore implements DataStore
{
    @Resource
    private DaoService daoService;

    public Object delete(Object object)
    {
        return null;
    }

    public Object get(String id)
    {
        return null;
    }

    @SuppressWarnings("unchecked")
    public List list(String containerId)
    {   
        return null;
    }

    public Object save(Object object)
    {
        return null;
    }

}
