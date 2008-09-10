package org.otherobjects.cms.util;

import java.util.List;

import org.otherobjects.cms.model.BaseNode;

public class TreeBuilder
{
    public List<TreeItem> buildTree(List<BaseNode> flat)
    {
        return null;
    }

    public class TreeItem
    {

        private BaseNode item;
        private List<BaseNode> children;

        protected BaseNode getItem()
        {
            return item;
        }

        protected void setItem(BaseNode item)
        {
            this.item = item;
        }

        protected List<BaseNode> getChildren()
        {
            return children;
        }

        protected void setChildren(List<BaseNode> children)
        {
            this.children = children;
        }
    }
}
