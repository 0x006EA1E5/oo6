package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class NavigationServiceImplTest extends TestCase
{
    private NavigationServiceImpl navigationService;
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        List<TreeNode> flat = new ArrayList<TreeNode>();

        flat.add(new TreeNode("/home.html"));
        flat.add(new TreeNode("/about/"));
        flat.add(new TreeNode("/about/history.html"));
        flat.add(new TreeNode("/products/"));
        flat.add(new TreeNode("/products/catgeories.html"));
        flat.add(new TreeNode("/products/kitchen/"));
        flat.add(new TreeNode("/products/kitchen/products.html"));
        flat.add(new TreeNode("/products/garden/"));
        flat.add(new TreeNode("/products/garden/products.html"));
        flat.add(new TreeNode("/contact/"));
        flat.add(new TreeNode("/contact/contact-us.html"));
        flat.add(new TreeNode("/terms.html"));

        TreeBuilder treeBuilder = new TreeBuilder();
        TreeNode tree = treeBuilder.buildTree(flat,new TreeNode("/"));
        
        navigationService = new NavigationServiceImpl();
        navigationService.tree = tree;

    }

    public void testGetNavigation()
    {
        assertEquals(5, navigationService.getNavigation("/", 1, 2).getChildren().size());
        assertEquals(1, navigationService.getNavigation("/about/", 1, 2).getChildren().size());
        assertEquals(3, navigationService.getNavigation("/products/", 1, 2).getChildren().size());
    }

}
