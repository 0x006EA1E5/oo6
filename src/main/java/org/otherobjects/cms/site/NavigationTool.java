package org.otherobjects.cms.site;

import java.util.List;

/**
 * FIXME Can we just @Tool annotate the service instead?
 * 
 * @author rich
 */
public class NavigationTool
{
    private NavigationService navigationService;

    public NavigationTool(NavigationService navigationService)
    {
        this.navigationService = navigationService;
    }

    public TreeNode getNavigation(String path, int startDepth, int endDepth)
    {
        return navigationService.getNavigation(path, startDepth, endDepth);
    }

    public List<TreeNode> getParents(String location)
    {
        return navigationService.getParents(location);
    }
}
