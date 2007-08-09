package org.otherobjects.cms.workbench;

import org.otherobjects.cms.model.DynaNode;

/**
 * Primary interface for manipulating site content.
 * 
 * @author rich
 */
public interface ContentService
{
    public DynaNode createItem(String container, String typeName);
    public DynaNode publishItem(String uuid);  
}
