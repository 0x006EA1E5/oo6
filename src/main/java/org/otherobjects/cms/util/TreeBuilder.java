package org.otherobjects.cms.util;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.model.BaseNode;

public class TreeBuilder
{
    public TreeItem buildTree(List<BaseNode> flat)
    {
        TreeItem tree = new TreeItem(null);

        for (BaseNode node : flat)
        {
            String path = node.getJcrPath();

            int lastSlash = path.lastIndexOf("/");
            String parentPath = path.substring(0, lastSlash);

            TreeItem parent = tree.getChild(parentPath);
            if (parent == null)
            {
                tree.getChildren().add(new TreeItem(node));
            }
            else
            {
                parent.getChildren().add(new TreeItem(node));
            }
        }
        return tree;
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

        public TreeItem getChild(String path)
        {
            return findChild(this, path);
        }

        private TreeItem findChild(TreeItem parent, String path)
        {
            if (this.children == null)
                return null;

            for (TreeItem c : parent.children)
            {
                if (c.getItem().getJcrPath().equals(path))
                    return c;
            }
            return null;
        }

    }
}
