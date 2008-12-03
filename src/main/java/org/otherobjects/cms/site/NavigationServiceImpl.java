package org.otherobjects.cms.site;

import java.util.List;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;

/**
 * TODO Sort order
 * TODO Don't store whole object: id, url, label. 
 * TODO Allow injection of other paths eg for non-jcr pages
 * 
 * @author rich
 *
 */
public class NavigationServiceImpl implements NavigationService
{
    private DaoService daoService;

    public Tree getNavigation(SiteNode location, int startDepth, int endDepth)
    {
        TreeBuilder tb = new TreeBuilder();

        Tree tree = tb.buildTree(getSiteNodes(), "/site/");

        
        Tree t = new Tree();
        t.setRootItem(tree.getNode("/site/learn/"));

        // Start at correct depth and location

        // Prune off unnecessarily deep branches

        return t;
    }

    public List<SiteNode> getParents(SiteNode location)
    {
        return null;
    }

    /**
     * Returns all folders and all items marked as showInNavigation.
     * 
     * @return
     */
    private List getSiteNodes()
    {
        // FIXME Need folder indicator
        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
        return universalJcrDao.getAllByJcrExpression("/jcr:root/site//element(*) [jcr:like(@ooType,'%Folder') or data/publishingOptions/@showInNavigation='true']");
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }
}
