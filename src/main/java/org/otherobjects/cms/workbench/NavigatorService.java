package org.otherobjects.cms.workbench;

import java.util.List;

/**
 * The navigator is the primary organisational interface
 * for the workbench.
 * 
 * <p>All items that may be selected or acted upon are
 * ultimately dervied from this.
 * 
 * <p>FIXME The terminology is terrible
 * <p>FIXME Pages and PageSets in the Navigtaor?
 * <p>TODO Think about publishing/workflow for this
 * <p>TODO Does this work with IDs or Paths?
 * 
 * @author rich
 */
public interface NavigatorService
{
   WorkbenchItem getRootItem();
   
   List<WorkbenchItem> getItemContents(String path);
   
   WorkbenchItem getItem(String path);
   
   WorkbenchItem addItem(String path, String name);
   
   void removeItem(String path);
   
   WorkbenchItem renameItem(String oldPath, String newPath);
   
   void moveItem(String itemId, String targetId, String point);
   
}
