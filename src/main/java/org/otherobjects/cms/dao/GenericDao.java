package org.otherobjects.cms.dao;

import java.io.Serializable;
import java.util.List;

import org.acegisecurity.annotation.Secured;


/**
 * Generic DAO (Data Access Object) with common methods to CRUD POJOs.
 *
 * <p>Extend this interface if you want typesafe (no casting necessary) DAO's for your
 * domain objects.
 *
 * @author AppFuse <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 */
@Secured({"ROLE_USER"})
public interface GenericDao <T, PK extends Serializable> {

    /**
     * Generic method used to get all objects of a particular type. This
     * is the same as lookup up all rows in a table.
     * @return List of populated objects
     */
    public List<T> getAll();
    
    
    public PagedResult<T> getAllPaged(int pageSize, int pageNo, String filterQuery, String sortField, boolean asc);
    
    public PagedResult<T> getPagedByQuery(String query, int pageSize, int pageNo, String filterQuery, String sortField, boolean asc);

    /**
     * Generic method to get an object based on class and identifier. An
     * ObjectRetrievalFailureException Runtime Exception is thrown if
     * nothing is found.
     *
     * @param id the identifier (primary key) of the object to get
     * @return a populated object
     * @see org.springframework.orm.ObjectRetrievalFailureException
     */
    @Secured({"AFTER_OWNERSHIP"})
    public T get(PK id);
    
    /**
     * Checks for existence of an object of type T using the id arg.
     * @param id
     * @return - true if it exists, false if it doesn't
     */
    public boolean exists(PK id);

    /**
     * Generic method to save an object - handles both update and insert.
     * @param object the object to save
     */
    public T save(T object);
    
    /**
     * Generic method to save an object - handles both update and insert.
     * @param object the object to save
     * @param validate - whether object should be validated before saving or not - if set to true object gets validated and not saved if validation fails
     */
    public T save(T object, boolean validate);

    /**
     * Generic method to delete an object based on class and id
     * @param id the identifier (primary key) of the object to remove
     */
    public void remove(PK id);
}