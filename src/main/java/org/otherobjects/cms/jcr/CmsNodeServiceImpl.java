package org.otherobjects.cms.jcr;

import java.util.List;

import org.apache.jackrabbit.ocm.spring.JcrMappingTemplate;
import org.otherobjects.cms.model.CmsNode;

public class CmsNodeServiceImpl implements CmsNodeService
{
    private JcrMappingTemplate template;

    public CmsNode getNode(String path)
    {
        return (CmsNode) template.getObject(path);
    }

    public CmsNode getNodesById(String type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<CmsNode> getNodesByType(String type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public JcrMappingTemplate getTemplate()
    {
        return template;
    }

    public void setTemplate(JcrMappingTemplate template)
    {
        this.template = template;
    }

}
