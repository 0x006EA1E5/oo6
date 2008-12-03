package org.otherobjects.cms.site;

import java.util.List;

import org.otherobjects.cms.model.BaseNode;

public class TreeBuilder
{
    public Tree buildTree(List<BaseNode> flat)
    {
        return buildTree(flat, "/");
    }

    public Tree buildTree(List<BaseNode> flat, String rootPath)
    {
        Tree tree = new Tree();
        ((RootTreeItem) tree.getRootItem()).setPath(rootPath);
        for (BaseNode node : flat)
        {
            tree.addItem(node);
        }
        return tree;
    }
}
