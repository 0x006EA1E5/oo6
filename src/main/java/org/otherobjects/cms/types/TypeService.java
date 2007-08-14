package org.otherobjects.cms.types;

import java.util.Collection;
import java.util.List;

import org.otherobjects.cms.model.SitePage;

/**
 * Maintains a register of all data types definitions (TypeDef) in the system.

 * <p>IDEA Could we register via annotations too?
 * 
 * @author rich
 */
public interface TypeService
{
    public void unregisterType(String typeName);
    public void registerType(TypeDef t);
    public TypeDef getType(String name);
    public TypeDef getTypeByClassName(String name);
    public Collection<TypeDef> getTypes();
    public Collection<TypeDef> getTypesBySuperClass(Class<?> superClass);
}
