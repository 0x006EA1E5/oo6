package org.otherobjects.cms.dao;

@SuppressWarnings("unchecked")
public interface DaoService
{
    GenericDao getDao(Class clazz);

    GenericDao getDao(String type);
    
    boolean hasDao(String type);
}
