package org.otherobjects.cms.dao;

import java.io.Serializable;
import java.util.List;

/**
 * @author rich
 */
public interface GenericDao<T, PK extends Serializable>
{
    /**
     * Saves the object. If the object already exists then it
     * is updated, otherwise it is inserted.
     */
    T save(T object);
    T save(T object, boolean validate);
    
    /**
     * If PK is null an assertion exceprtion is thrown.
     * 
     * @param id
     * @return
     */
    boolean exists(PK id);
    T get(PK id);
    
    // TODO Id or object?
    void remove(PK id);
    
    List<T> getAll();
    PagedList<T> getAllPaged(int pageSize, int pageNo, String filterQuery, String sortField, boolean asc);
    PagedList<T> getPagedByQuery(String query, int pageSize, int pageNo, String filterQuery, String sortField, boolean asc);
}
