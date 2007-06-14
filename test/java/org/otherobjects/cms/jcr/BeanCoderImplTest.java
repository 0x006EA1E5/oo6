package org.otherobjects.cms.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import junit.framework.TestCase;

import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;

public class BeanCoderImplTest extends TestCase
{
    private TypeService types;
    private BeanCoder beanCoder;
    
    @Override
    public void setUp()
    {
        types = new TypeService();
        beanCoder = new BeanCoderImpl();
        
        TypeDef td = new TypeDef("site_NewsStory");
        td.addProperty(new PropertyDef("title","string",null));
        types.registerType(td);
    }

    public void testConvertFromNode()
    {
    }

    public void testConvertToNode() throws RepositoryException
    {
        CmsNode cNode = new CmsNode("site_NewsStory");
        cNode.getData().put("title", "Story title");
        Node node = beanCoder.convertToNode(cNode);
        assertEquals("StoryTitle", node.getProperty("title"));
    }

    @Override
    public void tearDown()
    {

    }
}
