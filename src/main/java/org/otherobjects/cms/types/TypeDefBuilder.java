package org.otherobjects.cms.types;

public interface TypeDefBuilder
{
    TypeDef getTypeDef(String type) throws Exception;

    TypeDef getTypeDef(Class<?> clazz) throws Exception;
}
