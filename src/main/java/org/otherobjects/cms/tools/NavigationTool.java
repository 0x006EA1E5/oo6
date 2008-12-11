package org.otherobjects.cms.tools;

import java.util.List;

import javax.annotation.Resource;

import org.otherobjects.cms.site.NavigationService;
import org.otherobjects.cms.site.TreeNode;
import org.otherobjects.cms.views.Tool;
import org.springframework.stereotype.Component;

/**
 * FIXME Can we just @Tool annotate the service instead?
 * 
 * @author rich
 */
@Component
@Tool
public class NavigationTool
{
    @Resource
    private NavigationService navigationService;

    public NavigationTool()
    {
    }

    public TreeNode getNavigation(String path, int startDepth, int endDepth)
    {
        return this.navigationService.getNavigation(path, startDepth, endDepth);
    }

    public TreeNode getNavigation(String path, int startDepth, int endDepth, String currentPath)
    {
        return this.navigationService.getNavigation(path, startDepth, endDepth, currentPath);
    }

    public List<TreeNode> getTrail(String location, int startDepth, boolean foldersOnly)
    {
        return this.navigationService.getTrail(location, startDepth, foldersOnly);
    }

    public TreeNode getNode(String location, String currentPath)
    {
        return this.navigationService.getNode(location, currentPath);
    }
}
