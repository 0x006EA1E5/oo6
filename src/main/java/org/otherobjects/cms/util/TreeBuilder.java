package org.otherobjects.cms.util;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.model.BaseNode;

public class TreeBuilder
{
    public Tree buildTree(List<BaseNode> flat)
    {
        Tree tree = new Tree();

        for (BaseNode node : flat)
        {
            tree.addItem(node);
        }
        return tree;
    }

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
            if(node!=null)
                node.getChildren().add(new TreeItem(item));
        }

        public TreeItem getNode(String string)
        {
            return getNode(string, rootItem);
        }

        public TreeItem getNode(String string, TreeItem parent)
        {
            if(parent.getPath().equals(string))
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
                if(node!=null)
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

    public class TreeItem
    {

        private BaseNode item;
        private List<TreeItem> children = new ArrayList<TreeItem>();

        public TreeItem(BaseNode item)
        {
            this.item = item;
        }

        public BaseNode getItem()
        {
            return item;
        }

        public void setItem(BaseNode item)
        {
            this.item = item;
        }

        public List<TreeItem> getChildren()
        {
            return children;
        }

        public void setChildren(List<TreeItem> children)
        {
            this.children = children;
        }

        public String getPath()
        {
             String jcrPath = getItem().getJcrPath();
             if(!jcrPath.endsWith("/"))
                 jcrPath = jcrPath + "/";
             return jcrPath;
        }
        
        @Override
        public String toString()
        {
            return getPath();
        }

    }
    
    public class RootTreeItem extends TreeItem
    {
        public RootTreeItem()
        {
            super(null);
        }

        public String getPath()
        {
            return "/";
        }
    }
}
