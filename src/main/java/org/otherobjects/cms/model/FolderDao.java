package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.site.TreeNode;

public interface FolderDao extends GenericJcrDao<BaseNode>
{
    // FIXME Need Interface for all folders
    List<BaseNode> getFolders();
    TreeNode getFolderTree(String rootPath);
}
