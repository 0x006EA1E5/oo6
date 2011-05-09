package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.events.EventProxy;
import org.otherobjects.cms.events.ModificationEvent;
import org.otherobjects.cms.events.PublishEvent;
import org.otherobjects.cms.events.RootEventListener;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author rich
 */

@Service
@SuppressWarnings("unchecked")
public class NavigationServiceImpl implements NavigationService, RootEventListener, InitializingBean
{
    private final Logger logger = LoggerFactory.getLogger(NavigationServiceImpl.class);

    @Resource
    private DaoService daoService;

    @Resource
    private EventProxy eventProxy;

    protected TreeNode liveTree;
    protected TreeNode editTree;
    protected List<TreeNode> liveNodes;
    protected List<TreeNode> editNodes;

    protected Map<String, TreeNode> secureLiveNodes = new HashMap<String, TreeNode>();
    protected Map<String, TreeNode> secureEditNodes = new HashMap<String, TreeNode>();

    protected boolean testMode;

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

            buildTreeWithCacheCheck();

            // Start at correct depth and location by trimming path to correct
            // depth
            path = trimPath(path, startDepth);
            TreeNode startNode = getTree().getNode(path);

            // Clone tree but only to required depth
            if (startNode == null)
                throw new OtherObjectsException("Could not create navigation. Path does not exist: " + path);

            TreeNode clone = startNode.clone(endDepth - startDepth);

            // Mark selected nodes
            if (currentPath != null)
            {
                markSelected(clone, currentPath);
            }

            return clone;
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not create navigation tree.", e);
        }
    }

    public List<TreeNode> getTrail(String path, int startDepth, boolean foldersOnly)
    {
        try
        {
            Assert.isTrue(startDepth >= 0, "Navigation start depth must be >= 0");

            buildTreeWithCacheCheck();

            List<TreeNode> parents = new ArrayList<TreeNode>();
            int pos = 0;
            int depth = 0;
            while (pos < path.length())
            {
                pos = path.indexOf("/", pos) + 1;
                if (pos == 0)
                {
                    if (foldersOnly)
                    {
                        break;
                    }
                    else
                    {
                        pos = path.length();
                    }
                }
                String p = path.substring(0, pos);
                TreeNode node = getTree().getNode(p);
                if (node != null && depth++ >= startDepth)
                {
                    parents.add(node.clone(0));
                }
            }
            return parents;
        }
        catch (CloneNotSupportedException e)
        {
            throw new OtherObjectsException("Could not create nagivation trail.", e);
        }
    }

    /**
     * Finds nodes in tree which match the current path and flags them as
     * selected.
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
            {
                pos = path.length();
            }
            String p = path.substring(0, pos);
            TreeNode n = node.getNode(p);
            if (n != null)
            {
                n.setSelected(true);
            }
        }
    }

    /**
     * Trims a path to the specified depth. For example: / has a depth of 0,
     * /about/ has a depth of 1.
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
            {
                break;
            }
        }
        return path.substring(0, pos);
    }

    /**
     * Builds a tree representation of the navigational structure of the site.
     * This is an expensive operation and should only happen when tree structure
     * changes.
     * 
     * FIXME Need to synchronise this
     */
    private synchronized void buildTreeWithCacheCheck()
    {
        if (testMode)
            return;

        if(getNodes()==null || getSecureNodes()==null)
            buildTree();
    }

    private synchronized void buildTree()
    {
        TreeBuilder tb = new TreeBuilder();

        List<BaseNode> siteNodes = getSiteNodes();

        List<TreeNode> flat = new ArrayList<TreeNode>();
        Map<String, TreeNode> secured = new HashMap<String, TreeNode>();
        int count = 10000;
        for (BaseNode b : siteNodes)
        {
            String label = b.getOoLabel();
            if (b.hasProperty("publishingOptions.navigationLabel") && b.getPropertyValue("publishingOptions.navigationLabel") != null)
                label = (String) b.getPropertyValue("publishingOptions.navigationLabel");
            if (b.hasProperty("data.publishingOptions.navigationLabel") && b.getPropertyValue("data.publishingOptions.navigationLabel") != null)
                label = (String) b.getPropertyValue("data.publishingOptions.navigationLabel");

            int sortOrder = count++;
            if (b.hasProperty("publishingOptions.sortOrder"))
            {
                Long so = (Long) b.getPropertyValue("publishingOptions.sortOrder");
                if (so != null)
                    sortOrder = so.intValue();
            }
            if (b.hasProperty("data.publishingOptions.sortOrder"))
            {
                Long so = (Long) b.getPropertyValue("data.publishingOptions.sortOrder");
                if (so != null)
                    sortOrder = so.intValue();
            }
            TreeNode treeNode = new TreeNode(b.getOoUrlPath(), b.getId(), label, sortOrder);

            // folder security
            if (b.hasProperty("requiredRoles"))
            {
                treeNode.setRequiredRoles((List<String>) b.getPropertyValue("requiredRoles"));
                secured.put(treeNode.getPath(), treeNode);
            }

            flat.add(treeNode);
        }

        appendAdditionalNodes(flat);

        // Sort by path first which is a pre-requisite for the tree-builder
        Comparator<TreeNode> pathComparator = new PathComparator();
        Collections.sort(flat, pathComparator);

        setTree(tb.buildTree(flat, new TreeNode("/", null, "Home", 0)));
        setNodes(flat);
        setSecureNodes(secured);
    }

    public List<TreeNode> getAllNodes()
    {
        buildTreeWithCacheCheck();
        return getNodes();
    }


    public TreeNode getNode(String path, String currentPath)
    {
        try
        {
            buildTreeWithCacheCheck();

            // Mark selected nodes
            TreeNode node = getTree().getNode(path);

            node = node.clone(1);

            if (currentPath != null)
            {
                markSelected(node, currentPath);
            }

            return node;
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not create nagivation trail for path: " + path, e);
        }
    }

    /**
     * Overide this method to add site-specific nodes to the navigation tree.
     * 
     * @param flat
     */
    protected void appendAdditionalNodes(List<TreeNode> nodes)
    {
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
        return universalJcrDao.getAllByJcrExpression("/jcr:root/site//element(*) [((jcr:like(@ooType,'%Folder') and @inMenu='true') or publishingOptions/@showInNavigation='true')]");
    }

    /**
     * find out requires roles for given path
     * 
     * @param path
     * @return
     */
    public List<String> getRolesForPath(String path)
    {
        buildTreeWithCacheCheck();
        if (getSecureNodes().containsKey(path))
            return getSecureNodes().get(path).getRequiredRoles();

        return null;
    }

   
    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof PublishEvent)
        {
            this.liveNodes = null;
            this.secureLiveNodes = null;
            logger.debug("PublishEvent received. Clearing live Navigation cache.");
        }
        if (event instanceof ModificationEvent)
        {
            this.editNodes = null;
            this.secureEditNodes = null;
            logger.debug("ModificationEvent received. Clearing edit Navigation cache.");
        }
    }

    public void setEventProxy(EventProxy eventProxy)
    {
        this.eventProxy = eventProxy;
    }

    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(this.eventProxy, "To receive publishing events the event proxy needs to be injected");
        this.eventProxy.addRootEventListener(this);
    }
    

    protected TreeNode getTree()
    {
        if (SecurityUtil.isEditor())
            return editTree;
        else
            return liveTree;
    }
    
    protected void setTree(TreeNode tree)
    {
        if (SecurityUtil.isEditor())
            editTree = tree;
        else
            liveTree = tree;
    }
    
    protected List<TreeNode> getNodes()
    {
        if (SecurityUtil.isEditor())
            return editNodes;
        else
            return liveNodes;
    }

    protected void setNodes(List<TreeNode> nodes)
    {
        if (SecurityUtil.isEditor())
            editNodes = nodes;
        else
            liveNodes = nodes;
    }

    protected Map<String, TreeNode> getSecureNodes()
    {
        if (SecurityUtil.isEditor())
            return secureEditNodes;
        else
            return secureLiveNodes;
    }
    
    protected void setSecureNodes(Map<String, TreeNode> nodes)
    {
        if (SecurityUtil.isEditor())
            secureEditNodes = nodes;
        else
            secureLiveNodes = nodes;
    }

}
