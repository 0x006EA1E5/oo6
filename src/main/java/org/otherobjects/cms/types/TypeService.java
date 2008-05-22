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
    public void unregisterType(String typeName);

    public void registerType(TypeDef t);

    public TypeDef getType(String name);

    public TypeDef getType(Class clazz);

    public TypeDef getTypeByClassName(String name);

    public Collection<TypeDef> getTypes();

    public Collection<TypeDef> getTypesBySuperClass(Class<?> superClass);
}
