package org.otherobjects.cms.dao;

import org.otherobjects.cms.jcr.GenericJcrDaoJackrabbit;
import org.otherobjects.cms.model.DynaNode;

public class DynaNodeDaoJackrabbit extends GenericJcrDaoJackrabbit<DynaNode> implements DynaNodeDao
{
    public DynaNodeDaoJackrabbit()
    {
        super(DynaNode.class);
    }
}
