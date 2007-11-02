package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.workbench.NavigatorService;

public class SiteNavigatorServiceImpl implements SiteNavigatorService
{

    private NavigatorService navigatorService;
    private UniversalJcrDao universalJcrDao;

    public void setUniversalJcrDao(UniversalJcrDao universalJcrDao)
    {
        this.universalJcrDao = universalJcrDao;
    }

    public void setNavigatorService(NavigatorService navigatorService)
    {
        this.navigatorService = navigatorService;
    }

    public List<SiteItem> getSiteItems(SiteItem siteItem)
    {
        if (siteItem == null)
            siteItem = (SiteItem) navigatorService.getRootItem();

        List<SiteItem> result = new ArrayList<SiteItem>();

        for (BaseNode node : universalJcrDao.getAllByPath(siteItem.getJcrPath()))
        {
            if (node instanceof SiteItem)
                result.add((SiteItem) node);
        }

        return result;
    }

    public List<SiteItem> getSiteItems(SiteItem siteItem, int minLevel)
    {
        if (siteItem == null || minLevel == 0)
        {
            if (minLevel > 0)
                return null;
            else
                return getSiteItems(null); // return direct root children
        }

        int currentLevel = siteItem.getDepth();

        // currentlevel must not be more than one level up from parentLevel
        if (minLevel - currentLevel > 1)
            return null;

        if (currentLevel == minLevel)
        {
            if (!siteItem.isFolder()) // if currentlevel is parent of parentLevel it must be a folder for us to be able to return something
                return null;
            else
                return getSiteItems(siteItem);
        }

        //ok currentlevel not above or the same as the requested parentlevel so it is below the requested parentlevel
        // which means we need to walk up the tree from currentlevel until we reach parentlevel and then get the children
        while (siteItem.getDepth() != minLevel)
        {
            siteItem = getParentSiteItem(siteItem);
        }

        return getSiteItems(siteItem);
    }

    public SiteLineage getLineage(SiteItem siteItem)
    {
        List<SiteItem> siteItemLineage = new ArrayList<SiteItem>();
        siteItemLineage.add(siteItem);
        SiteItem parentItem = getParentSiteItem(siteItem);
        do
        {
            if (parentItem == null)
                break;
            siteItemLineage.add(parentItem);
            parentItem = getParentSiteItem(parentItem);
        }
        while (true);

        return new SiteLineageImpl(siteItemLineage);
    }

    private String removeLastPathPart(String path)
    {
        if (StringUtils.isBlank(path))
            return path;

        int lastPathDelim = path.lastIndexOf('/');
        return path.substring(0, lastPathDelim + 1); // leave a trailing slash
    }

    public SiteItem getParentSiteItem(SiteItem siteItem)
    {
        if (siteItem == null)
            return siteItem;

        if (siteItem.getDepth() == 0 || siteItem.getJcrPath().equals(SiteNavigatorService.JCR_SITE_ROOT_PATH)) //if we reached out site root stop moving up
            return null;

        String parentPath = removeLastPathPart(siteItem.getJcrPath());

        if (parentPath.equals(siteItem.getJcrPath()))
            return null;
        else
            return (SiteItem) universalJcrDao.getByPath(parentPath);
    }

}
