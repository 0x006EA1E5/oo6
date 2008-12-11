package org.otherobjects.cms.site;

import java.util.List;

public interface NavigationService
{
    TreeNode getNavigation(String path, int startDepth, int endDepth);

    TreeNode getNavigation(String path, int startDepth, int endDepth, String currentPath);

    List<TreeNode> getTrail(String path, int startDepth, boolean foldersOnly);

    List<TreeNode> getAllNodes();

    TreeNode getNode(String path, String currentPath);
}
