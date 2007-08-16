package org.otherobjects.cms.dao;

import java.util.List;

/**
 * Generic DAO (Data Access Object) with common methods to CRUD POJOs to JCR.
 *
 * @author rich
 */
public interface GenericJcrDao<T> extends GenericDao<T, String>
{
    /**
     * Generic method to get an object based on class and identifier. An
     * ObjectRetrievalFailureException Runtime Exception is thrown if
     * nothing is found.
     *
     * @param id the identifier (primary key) of the object to get
     * @return a populated object
     * @see org.springframework.orm.ObjectRetrievalFailureException
     */
    public T getByPath(String path);
    
    public T move(T node, String path);
    
    public List<T> getAllByPath(String path);
    
    public PagedResult<T> getPagedByPath(String path, int pageSize, int pageNo);
    
    public PagedResult<T> getPagedByPath(String path, int pageSize, int pageNo, String search, String sortField, boolean asc);
    
    public List<T> getAllByJcrExperssion(String xpath);
    
    public List<T> getVersions(T object);
    
    public T getVersionByChangeNumber(T object, int changeNumber);
    
    public T restoreVersionByChangeNumber(T object, int changeNumber, boolean removeExisting);

    public boolean existsAtPath(String path);

    public void moveItem(String itemId, String targetId, String point);
}