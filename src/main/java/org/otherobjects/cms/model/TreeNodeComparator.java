package org.otherobjects.cms.model;

import java.util.Comparator;

import org.otherobjects.cms.site.TreeNode;

public class TreeNodeComparator implements Comparator<TreeNode>
{
    public int compare(TreeNode n1, TreeNode n2)
    {
        return n1.getPath().compareTo(n2.getPath());
    }
}
