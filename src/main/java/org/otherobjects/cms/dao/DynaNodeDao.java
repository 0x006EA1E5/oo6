package org.otherobjects.cms.dao;

import java.util.List;

import org.otherobjects.cms.model.DynaNode;

public interface DynaNodeDao extends GenericJcrDao<DynaNode>
{
    public DynaNode create(String string);
    public List<DynaNode> getAllByType(String typeName);
    public void publish(DynaNode dynaNode);
}
