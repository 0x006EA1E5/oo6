package org.otherobjects.cms.dao;

import org.otherobjects.cms.model.DynaNode;

public interface DynaNodeDao extends GenericJcrDao<DynaNode>
{
    public DynaNode create(String string);
}
