package org.otherobjects.cms.dao;

import org.otherobjects.cms.jcr.GenericJcrDaoJackrabbit;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.TypeService;
import org.springframework.util.Assert;

public class DynaNodeDaoJackrabbit extends GenericJcrDaoJackrabbit<DynaNode> implements DynaNodeDao
{
    private TypeService typeService;

    public DynaNodeDaoJackrabbit()
    {
        super(DynaNode.class);
    }

    public DynaNode create(String typeName)
    {
        Assert.notNull(typeName, "typeName must not be null.");
        return new DynaNode(typeService.getType(typeName));
    }

    public TypeService getTypeService()
    {
        return typeService;
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }
}
