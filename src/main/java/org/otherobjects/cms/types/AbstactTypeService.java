package org.otherobjects.cms.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstactTypeService implements TypeService
{
    private Map<String, TypeDef> types = new HashMap<String, TypeDef>();

    public void registerType(TypeDef t)
    {
        types.put(t.getName(), t);
    }

    public void unregisterType(String typeName)
    {
        types.remove(typeName);
    }

    public TypeDef getType(String name)
    {
        return types.get(name);
    }

    public Collection<TypeDef> getTypes()
    {
        return (Collection<TypeDef>) types.values();
    }

    public void setTypes(Map<String, TypeDef> types)
    {
        this.types = types;
    }

    public TypeDef getTypeByClassName(String name)
    {
        for (TypeDef t : types.values())
        {
            if (t.getClassName().equals(name))
                return t;
        }
        return null;
    }
}
