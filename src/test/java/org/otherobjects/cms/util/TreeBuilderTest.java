package org.otherobjects.cms.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.model.BaseNode;

public class TreeBuilderTest extends TestCase
{

    public void testBuildTree()
    {
        List<BaseNode> flat = new ArrayList<BaseNode>();
        DynaNode node1 = new DynaNode();
        node1.setJcrPath("/1");
        flat.add(node1);
        DynaNode node11 = new DynaNode();
        node11.setJcrPath("/1/1");
        flat.add(node11);
        TreeBuilder treeBuilder = new TreeBuilder();
        List<TreeBuilder.TreeItem> tree = treeBuilder.buildTree(flat);
        
        assertEquals(1, tree.size());
    }

}
