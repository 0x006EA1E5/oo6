package org.otherobjects.cms.dao;

import org.otherobjects.cms.jcr.GenericJcrDaoJackrabbit;
import org.otherobjects.cms.model.CmsNode;

public class CmsNodeDaoJackrabbit extends GenericJcrDaoJackrabbit<CmsNode> implements CmsNodeDao
{
    public CmsNodeDaoJackrabbit()
    {
        super(CmsNode.class);
    }
}
