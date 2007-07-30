package org.otherobjects.cms.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstactTypeService implements TypeService
{
    private Logger logger = LoggerFactory.getLogger(AbstactTypeService.class);

    private Map<String, TypeDef> types = new HashMap<String, TypeDef>();

    public void registerType(TypeDef t)
    {
        // FIXME Allow re-registration of types
        if (!types.containsKey(t.getName()))
        {
            logger.info("Registering type: {}", t.getName());
            types.put(t.getName(), t);
            t.setTypeService(this);
        }
        else
        {
            logger.warn("Type already registered. Ignoring: {}", t.getName());
        }
    }

    public void unregisterType(String typeName)
    {
        logger.warn("No support for unregistering types yet. Ignoring: {}", typeName);
        //        types.remove(typeName);
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
