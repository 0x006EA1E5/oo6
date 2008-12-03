package org.otherobjects.cms.site;

import org.otherobjects.cms.model.BaseNode;

public class Tree
{
    private TreeItem rootItem = new RootTreeItem();

    public TreeItem getRootItem()
    {
        return rootItem;
    }

    public void setRootItem(TreeItem rootItem)
    {
        this.rootItem = rootItem;
    }

    public void addItem(BaseNode item)
    {
        TreeItem node = getNode(item.getPath(), rootItem);
        if (node != null)
            node.getChildren().add(new TreeItem(item));
    }

    public TreeItem getNode(String string)
    {
        return getNode(string, rootItem);
    }

    public TreeItem getNode(String string, TreeItem parent)
    {
        if (parent.getPath().equals(string))
            return parent;

        // TODO Don't look in dead ends

        // Search this item's children
        for (TreeItem ti : parent.getChildren())
        {
            if (ti.getPath().equals(string))
                return ti;
        }
        for (TreeItem ti : parent.getChildren())
        {
            TreeItem node = getNode(string, ti);
            if (node != null)
                return node;
        }

        return null;
    }

    public void print()
    {
        print(rootItem, 0);
    }

    public void print(TreeItem treeItem, int depth)
    {
        depth = depth + 1;
        String pad = "";
        for (int i = 0; i < depth; i++)
        {
            pad += ".";
        }
        System.out.println("[" + pad + (treeItem.getItem() != null ? treeItem.getItem().getCode() : "") + "]");
        for (TreeItem ti : treeItem.getChildren())
        {
            print(ti, depth);
        }
    }

}