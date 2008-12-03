package org.otherobjects.cms.site;

import java.util.List;

public interface NavigationService
{
    Tree getNavigation(SiteNode location, int startDepth, int endDepth);
    List<SiteNode> getParents(SiteNode location);
}
