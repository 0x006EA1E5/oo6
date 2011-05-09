package org.otherobjects.cms.dao;

import java.io.Serializable;

public interface DaoService
{
    <T extends Serializable> GenericDao<T, Serializable> getDao(Class<T> clazz);

    <T extends Serializable> GenericDao<T, Serializable> getDao(String type);
    
    boolean hasDao(String type);

    <T extends Serializable> void addDao(String name, GenericDao<T, Serializable> dao);
    <T extends Serializable> void addDao(GenericDao<T, Serializable> dao);
}
