package org.otherobjects.cms.model;

/**
 * A CmsNode object represents a data node in the content repository.
 * 
 * <p>A node can be uniquely identified in 2 ways: by a GUID or by the jcrPath
 * (which is the concatenation of path and code).
 * 
 * <p>TODO Add support for description, icon and image generators
 */
public interface CmsNode
{
    //TODO Only needed until OCM set's id on insert itself
    public void setId(String id);
   
    public String getId();

    public String getJcrPath();

    public String getLabel();
}
