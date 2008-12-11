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
        TreeNode tree = treeBuilder.buildTree(flat, new TreeNode("/"));

        this.navigationService = new NavigationServiceImpl();
        this.navigationService.tree = tree;
    }

    public void testGetNavigation()
    {
        // Test start depths
        assertEquals(5, this.navigationService.getNavigation("/", 0, 1).getChildren().size());
        assertEquals(1, this.navigationService.getNavigation("/about/", 1, 2).getChildren().size());
        assertEquals(3, this.navigationService.getNavigation("/products/", 1, 2).getChildren().size());
        assertEquals(5, this.navigationService.getNavigation("/about/", 0, 1).getChildren().size());

        // Test end depths
        assertNotNull(this.navigationService.getNavigation("/", 0, 1).getNode("/about/"));
        assertEquals(0, this.navigationService.getNavigation("/", 0, 1).getNode("/about/").getChildren().size());
        assertNotNull(this.navigationService.getNavigation("/products/kitchen/", 1, 2).getNode("/products/kitchen/"));
        assertEquals(0, this.navigationService.getNavigation("/products/kitchen/", 1, 2).getNode("/products/kitchen/").getChildren().size());

        // Test selected nodes
        assertTrue(this.navigationService.getNavigation("/products/kitchen/", 1, 2, "/products/kitchen/product.html").getNode("/products/kitchen/").isSelected());

        // Test out of range
        try
        {
            assertNull(this.navigationService.getNavigation("", 1, 2).getNode("/"));
            fail();// We should get an exception before this line
        }
        catch (Exception e)
        {
        }
    }

    public void testTrimPath()
    {
        assertEquals("/", this.navigationService.trimPath("/", 0));
        assertEquals("/", this.navigationService.trimPath("/about/", 0));
        assertEquals("/", this.navigationService.trimPath("/about/test.html", 0));
        assertEquals("/about/", this.navigationService.trimPath("/about/", 1));
        assertEquals("/about/", this.navigationService.trimPath("/about/this/test.html", 1));
        assertEquals("/about/", this.navigationService.trimPath("/about/test.html", 1));
    }

    public void testMarkSelected()
    {
        TreeNode node = this.navigationService.getNavigation("/products/kitchen/products.html", 0, 3);
        this.navigationService.markSelected(node, "/products/kitchen/products.html");
        assertTrue(node.isSelected());
        assertTrue(node.getNode("/products/").isSelected());
        assertTrue(node.getNode("/products/kitchen/").isSelected());
        assertTrue(node.getNode("/products/kitchen/products.html").isSelected());
        assertFalse(node.getNode("/about/").isSelected());
    }

    public void testGetTrail()
    {
        List<TreeNode> trail = this.navigationService.getTrail("/products/kitchen/products.html", 0, false);
        assertEquals(4, trail.size());
        assertEquals("/", trail.get(0).getPath());
        assertEquals("/products/kitchen/products.html", trail.get(3).getPath());

        trail = this.navigationService.getTrail("/products/kitchen/products.html", 0, true);
        assertEquals("/products/kitchen/", trail.get(2).getPath());
        assertEquals(3, trail.size());

        trail = this.navigationService.getTrail("/products/kitchen/products.html", 1, true);
        assertEquals(2, trail.size());
        assertEquals("/products/", trail.get(0).getPath());
    }
}
