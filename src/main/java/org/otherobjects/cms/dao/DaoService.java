package org.otherobjects.cms.dao;

@SuppressWarnings("unchecked")
public interface DaoService
{
    public GenericDao getDao(Class clazz);

    public GenericDao getDao(String type);

}
