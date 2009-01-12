package org.otherobjects.cms.site;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.emory.mathcs.backport.java.util.Collections;

public class TreeBuilder
{
    public TreeNode buildTree(List<TreeNode> flat)
    {
        return buildTree(flat, "/");
    }

    public TreeNode buildTree(List<TreeNode> flat, String rootPath)
    {
        TreeNode tree = new TreeNode(rootPath);
        return buildTree(flat, tree);
    }

    public TreeNode buildTree(List<TreeNode> flat, TreeNode tree)
    {
        for (TreeNode node : flat)
        {
            addItemToTree(tree, node);
        }
        return tree;
    }

    private void addItemToTree(TreeNode tree, TreeNode node)
    {
        String parentPath = node.getPath();
        if (parentPath.endsWith("/"))
            parentPath = parentPath.substring(0, parentPath.length() - 1);
        parentPath = StringUtils.substringBeforeLast(parentPath, "/") + "/";

        TreeNode parent = tree.getNode(parentPath);
        if (parent != null)
        {
            parent.getChildren().add(node);
            // FIXME URGENT This is v bad performance wise
            Collections.sort(parent.getChildren());
        }
    }

}
