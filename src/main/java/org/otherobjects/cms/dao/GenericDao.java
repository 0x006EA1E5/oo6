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
    public T save(T object);
    public T save(T object, boolean validate);
    
    /**
     * If PK is null an assertion exceprtion is thrown.
     * 
     * @param id
     * @return
     */
    public boolean exists(PK id);
    public T get(PK id);
    
    // TODO Id or object?
    public void remove(PK id);
    
    public List<T> getAll();
    public PagedList<T> getAllPaged(int pageSize, int pageNo, String filterQuery, String sortField, boolean asc);
    public PagedList<T> getPagedByQuery(String query, int pageSize, int pageNo, String filterQuery, String sortField, boolean asc);
}