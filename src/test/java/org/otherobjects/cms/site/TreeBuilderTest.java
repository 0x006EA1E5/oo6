package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TreeBuilderTest extends TestCase
{

    public void testBuildTree()
    {
        List<TreeNode> flat = new ArrayList<TreeNode>();
        
        flat.add(new TreeNode("/1/"));
        flat.add(new TreeNode("/1/1/"));
        flat.add(new TreeNode("/1/1/1/"));
        flat.add(new TreeNode("/1/2/"));
        flat.add(new TreeNode("/2/"));

        TreeBuilder treeBuilder = new TreeBuilder();
        TreeNode tree = treeBuilder.buildTree(flat);
        tree.print();

        assertEquals(2, tree.getNode("/").getChildren().size());
        assertEquals(2, tree.getNode("/1/").getChildren().size());
    }
}
