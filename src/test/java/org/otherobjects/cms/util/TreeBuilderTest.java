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

        DynaNode node111 = new DynaNode();
        node111.setJcrPath("/1/1/1");
        flat.add(node111);

        DynaNode node12 = new DynaNode();
        node12.setJcrPath("/1/2");
        flat.add(node12);

        DynaNode node2 = new DynaNode();
        node2.setJcrPath("/2");
        flat.add(node2);

        TreeBuilder treeBuilder = new TreeBuilder();
        TreeBuilder.Tree tree = treeBuilder.buildTree(flat);
        tree.print();

        assertEquals(2, tree.getNode("/").getChildren().size());
        assertEquals(2, tree.getNode("/1/").getChildren().size());
    }
}
