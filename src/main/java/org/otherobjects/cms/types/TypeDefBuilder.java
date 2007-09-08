package org.otherobjects.cms.types;

public interface TypeDefBuilder {

	public abstract TypeDef getTypeDef(String type) throws Exception;

	public abstract TypeDef getTypeDef(Class<?> clazz) throws Exception;

}