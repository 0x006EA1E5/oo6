package org.otherobjects.cms.site;

import java.util.List;

public interface NavigationService
{
    TreeNode getNavigation(String path, int startDepth, int endDepth);
    List<TreeNode> getParents(String path);
}
