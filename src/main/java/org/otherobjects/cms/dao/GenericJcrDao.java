/* $$ This file has been instrumented by Clover 1.3.13#20070503123026657 $$ */package org.otherobjects.cms.dao;

import java.util.List;

import org.apache.jackrabbit.ocm.spring.JcrMappingTemplate;

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
    
    public List<T> getAllByPath(String path);
    
    public List<T> getAllByJcrExperssion(String xpath);

    public boolean existsAtPath(String path);

    // FIXME Is this a good idea?
    public JcrMappingTemplate getJcrMappingTemplate();
}