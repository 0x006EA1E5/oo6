package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class TreeNode
{
    private String id;
    private String path;
    private String label;
    private Object object;
    private List<TreeNode> children = new ArrayList<TreeNode>();

    public TreeNode()
    {
    }

    public TreeNode(String path)
    {
        this.path = path;
    }

    public TreeNode(String path, String id, String label)
    {
        this.path = path;
        this.id = id;
        this.label = label;
    }

    public TreeNode(String path, String id, String label, Object object)
    {
        this.path = path;
        this.id = id;
        this.label = label;
        this.object = object;
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
}