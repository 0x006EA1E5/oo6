package org.otherobjects.cms.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Base DAO interface for objects persisted in the JCR content repository. This
 * extends the basic DAO methods in <code>GenericDao</code>.
 * 
 * <p>Which workspace are we referring to at any given time?
 * 
 * <p>All JCR-backed objects are versioned and kept in one of two workspaces. 
 * Change numbers. Live/edit.
 * 
 * @author rich
 */
public interface GenericJcrDao<T> extends GenericDao<T, Serializable>
{
    String REORDER_BELOW = "below";
    String REORDER_ABOVE = "above";
    String REORDER_APPEND = "append";

    /**
     * Tests to see if a object is present in the current workspace at
     * the path specified.
     * 
     * @param path the full path to test
     * @return true if the object exists
     */
    boolean existsAtPath(String path);

    /**
     * Returns the object at the specified path. If there is no object at
     * the path then an <code>ObjectRetrievalFailureException</code> is
     * thrown.
     * 
     * @param path the path of the object
     * @return the object
     */
    T getByPath(String path);

    /**
     * Renames an object by moving it to new path in the repository.
     * 
     * <p>TODO Does this increment the change number? It should.
     * <p>FIXME Can we detect path changes automatically? If so this is redundant.
     */
    T rename(T node, String path);

    /**
     * Changes the posisition of a node in the workspace.
     * 
     * <p>The position should be one of <code>REORDER_ABOVE</code>, <code>REORDER_BELOW</code>
     * or <code>REORDER_APPEND</code>. <code>REORDER_APPEND</code> is used to add the object
     * to the target object (assuming the target is a container). 
     * 
     * @param object the object to reorder
     * @param target the object to move relative to
     * @param position the position relative to the target to move the object to
     * @return
     */
    T reorder(T object, T target, String position);

    /**
     * Publishes the object to the live workspace.
     * 
     * <p>A message can be provided to supply additional information about the 
     * publishing reason which will be stored with the published object. The message
     * should be a single sentence and not contain line breaks.
     * 
     * <p>TODO Enforce message format?
     * 
     * @param object the object to be published
     * @param message an optional message
     */
    void publish(T object, String message);
    
    /**
     * Unpublishes the object from the live workspace.
     * 
     * <p>A message can be provided to supply additional information about the 
     * unpublishing reason which will be stored with the unpublished object. The message
     * should be a single sentence and not contain line breaks.
     * 
     * @param object the object to be unpublished
     * @param message an optional message
     */
     void unpublish(T object, String message);

    /**
     * Returns a list of all published versions for the provided object.
     * An unpublished object will return an empty list. The latest edit
     * to an object will not be included in this list as ist is unpublished.
     * 
     * @param object the object to fetch versions for
     * @return a list of all published versions of this object
     */
    List<T> getVersions(T object);

    /**
     * Returns a previous version based on its change number. 
     * 
     * <p>TODO If there is no version that matches then an unchecked exception 
     * is thrown.
     * 
     * @param object the objct to fetch the version for
     * @param changeNumber the change number
     * @return the requested version
     */
    T getVersionByChangeNumber(T object, int changeNumber);

    /**
     * Rolls back an object to a previous state.
     * 
     * <p>TODO If there is no version that matches then an unchecked exception 
     * is thrown.
     * 
     * @param object the object to roll back
     * @param changeNumber the change number of the version to roll back to
     * @return
     */
    T restoreVersionByChangeNumber(T object, int changeNumber);

    List<T> getAllByPath(String path);

    PagedList<T> getPagedByPath(String path, int pageSize, int pageNo);

    PagedList<T> getPagedByPath(String path, int pageSize, int pageNo, String search, String sortField, boolean asc);

    T getByJcrExpression(String expression);

    List<T> getAllByJcrExpression(String expression);
    List<T> getAllByJcrExpression(String expression, final boolean useEdit);

    PagedList<T> pageByJcrExpression(String expression, int pageSize, int pageNo);

    void renderNodeInfo(String id);
    
}
