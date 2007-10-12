package org.otherobjects.cms.workbench;

import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.model.CmsNode;

/**
 * Primary interface for manipulating site content.
 * 
 * @author rich
 */
public interface ContentService
{
    public CmsImage createImage(String service, String id);

    public CmsNode createItem(String container, String typeName);

    public CmsNode publishItem(String uuid, String message);
}
