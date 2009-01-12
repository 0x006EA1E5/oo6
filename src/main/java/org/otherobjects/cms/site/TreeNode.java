package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TreeNode implements Cloneable, Comparable<TreeNode>
{
    private String id;
    private String path;
    private String redirectPath;
    private String label;
    private int sortOrder;
    private Object object;
    private List<TreeNode> children = new ArrayList<TreeNode>();
    private boolean selected = false;
    private Date modificationTimestamp = new Date(); // For SEO sitemap

    public TreeNode()
    {
    }

    public TreeNode(String path)
    {
        this.path = path;
    }

    public TreeNode(String path, String id, String label, int sortOrder)
    {
        this.path = path;
        this.id = id;
        this.label = label;
        this.sortOrder = sortOrder;
    }

    public TreeNode(String path, String id, String label, int sortOrder, String redirectPath)
    {
        this.path = path;
        this.id = id;
        this.label = label;
        this.redirectPath = redirectPath;
        this.sortOrder = sortOrder;
    }

    public TreeNode(String path, String id, String label, int sortOrder, Object object)
    {
        this.path = path;
        this.id = id;
        this.label = label;
        this.object = object;
        this.sortOrder = sortOrder;
    }

    public int compareTo(TreeNode node)
    {
        Integer nodeSortOrder = node.getSortOrder();
        return - nodeSortOrder.compareTo(this.getSortOrder());
    }
    
    public int getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    /**
     * Clones the tree but only to the maximum depth specified.
     * @param depth
     * @return
     */
    public TreeNode clone(int depth) throws CloneNotSupportedException
    {
        TreeNode clone = (TreeNode) super.clone();
        if (depth > 0)
        {
            List<TreeNode> childrenClone = new ArrayList<TreeNode>();
            for (TreeNode child : getChildren())
            {
                childrenClone.add((TreeNode) child.clone(depth - 1));
            }
            clone.setChildren(childrenClone);
        }
        else
        {
            // Remove pointer to original list of children
            clone.setChildren(new ArrayList<TreeNode>());
        }
        return clone;
    }

    public TreeNode getNode(String string)
    {
        return getNode(string, this);
    }

    public TreeNode getNode(String path, TreeNode parent)
    {
        if (parent.getPath().equals(path))
            return parent;

        // TODO Optimize: Don't look in dead ends

        // Search this item's children
        for (TreeNode ti : parent.getChildren())
        {
            if (ti.getPath().equals(path))
                return ti;
        }
        for (TreeNode ti : parent.getChildren())
        {
            TreeNode node = getNode(path, ti);
            if (node != null)
                return node;
        }

        return null;
    }

    public void print()
    {
        print(this, 0);
    }

    public void print(TreeNode node, int depth)
    {
        depth = depth + 1;
        String pad = "";
        for (int i = 0; i < depth; i++)
        {
            pad += ".";
        }
        System.out.println("[" + pad + (node.getPath()) + "]");
        for (TreeNode ti : node.getChildren())
        {
            print(ti, depth);
        }
    }

    public String getUrl()
    {
        // FIXME Deal with context path here?
        if (getRedirectPath() != null)
            return getRedirectPath();
        else
            return getPath();
    }

    @Override
    public String toString()
    {
        return getPath();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public List<TreeNode> getChildren()
    {
        return children;
    }

    public void setChildren(List<TreeNode> children)
    {
        this.children = children;
    }

    public Object getObject()
    {
        return object;
    }

    public void setObject(Object object)
    {
        this.object = object;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public String getRedirectPath()
    {
        return redirectPath;
    }

    public void setRedirectPath(String redirectPath)
    {
        this.redirectPath = redirectPath;
    }

    public Date getModificationTimestamp()
    {
        return modificationTimestamp;
    }

    public void setModificationTimestamp(Date modificationTimestamp)
    {
        this.modificationTimestamp = modificationTimestamp;
    }
}
