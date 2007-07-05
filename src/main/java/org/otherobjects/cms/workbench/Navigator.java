package org.otherobjects.cms.workbench;

import java.util.List;

/**
 * The navigator is the primary organisational interface
 * for the workbench.
 * 
 * <p>All items that may be selected or acted upon are
 * ultimately dervied from this.
 * 
 * <p>TODO Think about publishing/workflow for this
 * 
 * @author rich
 */
public interface Navigator
{
   public WorkbenchItem getRootItem();
   
   public List<WorkbenchItem> getItemContents(String path);
   
   public WorkbenchItem getItem(String path);
   
   public void addItem(String path, WorkbenchItem newItem);
   
   public void removeItem(String path);
   
   public WorkbenchItem moveItem(String oldPath, String newPath);
}
