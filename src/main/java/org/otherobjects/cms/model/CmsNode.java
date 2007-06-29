package org.otherobjects.cms.model;

/**
 * Base interface that all objcts stored via OCM must implement.
 * 
 * <p>A node can be uniquely identified in 2 ways: by a GUID or by the jcrPath
 * (which is the concatenation of path and code).
 */
public interface CmsNode
{
    //TODO Only needed until OCM set's id on insert itself
    public void setId(String id);
   
    public String getId();

    public String getJcrPath();

    public String getLabel();
}
