package org.otherobjects.cms.dao;

import java.util.Map;

@SuppressWarnings("unchecked")
public class DaoServiceImpl implements DaoService
{
    private static final String DYNA_NODE_DAO_KEY = "org.otherobjects.cms.model.DynaNode";
    private Map<String, GenericDao> daoMap;

    public GenericDao getDao(Class clazz)
    {
        return getDao(clazz.getName());
    }

    public GenericDao getDao(String type)
    {
        GenericDao dao = daoMap.get(type);
        if(dao==null)
        {
            // If no specific dao found then use dynaNode Dao
            dao = daoMap.get(DYNA_NODE_DAO_KEY);
        }
        return dao;
    }

    public Map<String, GenericDao> getDaoMap()
    {
        return daoMap;
    }

    public void setDaoMap(Map<String, GenericDao> daoMap)
    {
        this.daoMap = daoMap;
    }
}
