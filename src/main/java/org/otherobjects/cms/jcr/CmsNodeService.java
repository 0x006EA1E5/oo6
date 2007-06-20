package org.otherobjects.cms.jcr;

import java.util.List;

import org.otherobjects.cms.model.CmsNode;

public interface CmsNodeService
{
    public CmsNode getNode(String path);
    public CmsNode getNodesById(String type);
    public List<CmsNode> getNodesByType(String type);
}
