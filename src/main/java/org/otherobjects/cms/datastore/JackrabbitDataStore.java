package org.otherobjects.cms.datastore;

import java.util.List;

import javax.annotation.Resource;

import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.types.TypeDef;

public class JackrabbitDataStore implements DataStore
{
    @Resource
    private UniversalJcrDao universalJcrDao;

    public Object create(TypeDef typeDef)
    {
        return universalJcrDao.create(typeDef.getName());
    }

    public Object get(String id)
    {
        return universalJcrDao.get(id);
    }

    public Object delete(Object object)
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
