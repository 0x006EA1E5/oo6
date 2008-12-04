package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.springframework.util.Assert;

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

    public NavigationServiceImpl()
    {
    }

    public TreeNode getNavigation(String path, int startDepth, int endDepth)
    {
        return getNavigation(path, startDepth, endDepth, null);
    }

    public TreeNode getNavigation(String path, int startDepth, int endDepth, String currentPath)
    {
        try
        {
            Assert.isTrue(startDepth >= 0, "Navigation start depth must be >= 0");
            Assert.isTrue(endDepth > startDepth, "Navigation end depth must be > start depth");

            // FIXME Need to synchronise this
            //if (tree == null)
                buildTree();

            // Start at correct depth and location by trimming path to correct depth
            path = trimPath(path, startDepth);
            TreeNode startNode = (TreeNode) tree.getNode(path);

            // Clone tree but only to required depth
            TreeNode clone = (TreeNode) startNode.clone(endDepth - startDepth);

            // Mark selected nodes
            if (currentPath != null)
                markSelected(clone, currentPath);

            return clone;
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not create nagivation tree.", e);
        }
    }

    /**
     * Finds nodes in tree which match the current path and flags them as selected. 
     * 
     * @param clone
     * @param currentPath
     */
    protected void markSelected(TreeNode node, String path)
    {
        int pos = 0;
        while (pos < path.length())
        {
            pos = path.indexOf("/", pos) + 1;
            if (pos == 0)
                pos = path.length();
            String p = path.substring(0, pos);
            TreeNode n = node.getNode(p);
            if (n != null)
                n.setSelected(true);
        }
    }

    /**
     * Trims a path to the correct depth. For example: / has a depth of 0, /about/ has a depth of 1.
     * 
     * @param path
     * @param startDepth
     * @return
     */
    protected String trimPath(String path, int startDepth)
    {
        int pos = 0;
        int depth = 0;
        while (pos < path.length())
        {
            pos = path.indexOf("/", pos) + 1;
            if (++depth > startDepth)
                break;
        }
        return path.substring(0, pos);
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
            flat.add(new TreeNode(b.getOoUrlPath(), b.getId(), label));
        }

        appendAdditionalNodes(flat);

        this.tree = tb.buildTree(flat, new TreeNode("/",null,"Home"));
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

    public List<TreeNode> getTrail(String path)
    {
        try
        {
            List<TreeNode> parents = new ArrayList<TreeNode>();
            int pos = 0;
            while (pos < path.length())
            {
                pos = path.indexOf("/", pos) + 1;
                if (pos == 0)
                    break;
                    //pos = path.length();
                String p = path.substring(0, pos);
                TreeNode node = tree.getNode(p);
                if(node!=null)
                    parents.add((TreeNode) node.clone(0));
            }
            return parents;
        }
        catch (CloneNotSupportedException e)
        {
            throw new OtherObjectsException("Could not create nagivation trail.", e);
        }
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
