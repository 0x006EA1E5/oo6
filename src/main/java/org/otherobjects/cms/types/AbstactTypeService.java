package org.otherobjects.cms.types;

import java.util.Collection;
import java.util.Map;

public abstract class AbstactTypeService implements TypeService
{
    private Map<String, TypeDef> types;

    public void registerType(TypeDef t)
    {
        types.put(t.getName(), t);
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
}
