package org.otherobjects.cms.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.otherobjects.cms.OtherObjectsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

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
        TypeDef typeDef = types.get(name);
        Assert.notNull(typeDef, "Type not found: " + name);
        return typeDef;
        
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
    
    public Collection<TypeDef> getTypesBySuperClass(Class<?> superClass)
    {
        try
        {
            // FIXME Is there an alternative to instatiating classes here?
            List<TypeDef> matches = new ArrayList<TypeDef>();
            for (TypeDef t : types.values())
            {
                Class cls = Class.forName(t.getSuperClassName());
                if (superClass.isAssignableFrom(cls))
                    matches.add(t);
            }
            return matches;
        }
        catch (ClassNotFoundException e)
        {
            throw new OtherObjectsException("Super class not found");
        }
    }
}
