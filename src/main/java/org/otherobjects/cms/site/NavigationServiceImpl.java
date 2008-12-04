package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;

/**
 * TODO Sort order
 * TODO Dealing with default pages (don't duplicate with folder)
 * TODO Don't store whole object: id, url, label. 
 * TODO Allow injection of other paths eg for non-jcr pages
 * 
 * @author rich
 *
 */
@SuppressWarnings("unchecked")
public class NavigationServiceImpl implements NavigationService
{
    private DaoService daoService;

    protected TreeNode tree;

    public TreeNode getNavigation(String path, int startDepth, int endDepth)
    {
        // FIXME Need to synchronise this
        //if (tree == null)
            buildTree();

        // Start at correct depth and location

        // Prune off unnecessarily deep branches

        path = path.substring(5);
            
        return tree.getNode(path);
    }

    /**
     * Builds a tree representation of the navigational structure of the site. This is an expensive
     * operation and should only happen when tree structure changes.
     */
    private void buildTree()
    {
        TreeBuilder tb = new TreeBuilder();

        List<BaseNode> siteNodes = getSiteNodes();

        List<TreeNode> flat = new ArrayList<TreeNode>();
        for (BaseNode b : siteNodes)
        {
            String label = (String) (b.hasProperty("data.publishingOptions.navigationLabel") && b.getPropertyValue("data.publishingOptions.navigationLabel") != null ? b
                    .getPropertyValue("data.publishingOptions.navigationLabel") : b.getOoLabel());
            String path = b.getJcrPath().contains(".") ? b.getJcrPath() : b.getJcrPath() + "/"; // FIXME Need to clear up this Jcr Path stuff.
            path = path.substring(5); // Remove /site so we have proper URLs
            flat.add(new TreeNode(path, b.getId(), label));
        }

        appendAdditionalNodes(flat);

        this.tree = tb.buildTree(flat, "/");
    }

    /**
     * Overide this method to add site-specific nodes to the navigation tree.
     * 
     * @param flat
     */
    protected void appendAdditionalNodes(List<TreeNode> nodes)
    {
        nodes.add(new TreeNode("/engage/survey", "", "Survey"));

    }

    public List<TreeNode> getParents(String path)
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
