package org.otherobjects.cms.types;

import java.util.Collection;

/**
 * Maintains a register of all data types definitions (TypeDef) in the system.
 * 
 * @author rich
 */
@SuppressWarnings("unchecked")
public interface TypeService
{
    void unregisterType(String typeName);

    void registerType(TypeDef t);

    TypeDef getType(String name);

    TypeDef getType(Class clazz);

    TypeDef getTypeByClassName(String name);

    Collection<TypeDef> getTypes();

    Collection<TypeDef> getTypesBySuperClass(Class<?> superClass);
}
