package org.otherobjects.cms.jcr;

import javax.jcr.Node;

import org.otherobjects.cms.model.CmsNode;

/**
 * Converts a bean to and from JCR Nodes. Beans must be of a 
 * type registered in the TypesService.
 * 
 * <p>Beans bay be real beans, Groovy beans, or dynamic 
 * types.
 * 
 * @author rich
 */
public interface BeanCoder
{
    public Node convertToNode(CmsNode bean);
    public CmsNode convertFromNode(Node bean);
}
