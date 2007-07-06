package org.otherobjects.cms.types;

import org.otherobjects.cms.jcr.GenericJcrDaoJackrabbit;

public class TypeDefDaoJackrabbit extends GenericJcrDaoJackrabbit<TypeDef> implements TypeDefDao
{

    public TypeDefDaoJackrabbit()
    {
        super(TypeDef.class);
    }

}
