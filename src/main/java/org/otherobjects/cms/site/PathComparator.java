package org.otherobjects.cms.site;

import java.util.Comparator;

public class PathComparator implements Comparator<TreeNode>
{
    public int compare(TreeNode o1, TreeNode o2)
    {
        return o1.getPath().compareTo(o2.getPath());
    }
}
