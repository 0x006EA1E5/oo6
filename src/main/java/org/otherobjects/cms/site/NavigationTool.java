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

    public Tree getNavigation(SiteNode location, int startDepth, int endDepth)
    {
        return navigationService.getNavigation(location, startDepth, endDepth);
    }

    public List<SiteNode> getParents(SiteNode location)
    {
        return navigationService.getParents(location);
    }
}
