package org.otherobjects.cms.site;

import java.util.List;

import javax.annotation.Resource;

import org.otherobjects.cms.views.Tool;
import org.springframework.stereotype.Component;

/**
 * FIXME Can we just @Tool annotate the service instead?
 * 
 * @author rich
 */
@Component
@Tool("navigationTool")
public class NavigationTool
{
    @Resource
    private NavigationService navigationService;

    public NavigationTool()
    {
    }

    public TreeNode getNavigation(String path, int startDepth, int endDepth)
    {
        return navigationService.getNavigation(path, startDepth, endDepth);
    }

    public TreeNode getNavigation(String path, int startDepth, int endDepth, String currentPath)
    {
        return navigationService.getNavigation(path, startDepth, endDepth, currentPath);
    }

    public List<TreeNode> getTrail(String location, int startDepth, boolean foldersOnly)
    {
        return navigationService.getTrail(location, startDepth, foldersOnly);
    }
}
