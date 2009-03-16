package org.otherobjects.cms.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.otherobjects.cms.OtherObjectsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public abstract class AbstractTypeService implements TypeService
{
    private final Logger logger = LoggerFactory.getLogger(AbstractTypeService.class);

    private Map<String, TypeDef> types = new HashMap<String, TypeDef>();

    public void registerType(TypeDef t)
    {
        // FIXME Allow re-registration of types
        if (this.types.containsKey(t.getName()))
        {
            this.logger.warn("Type already registered. Replacing existing one. Name: {}", t.getName());
        }
        this.logger.info("Registering type: {}", t.getName());
        this.types.put(t.getName(), t);
        t.setTypeService(this);
    }

    public void unregisterType(String typeName)
    {
        this.logger.warn("No support for unregistering types yet. Ignoring: {}", typeName);
        //        types.remove(typeName);
    }

    /**
     * @return TypeDef or null if no typeDef found
     */
    public TypeDef getType(String name)
    {
        TypeDef typeDef = this.types.get(name);
        // FIXME Turn this back on somewhere
        // Assert.notNull(typeDef, "Type not found: " + name);
        return typeDef;
    }

    /**
     * @return TypeDef or null if no typeDef found
     */
    public TypeDef getType(Class clazz)
    {
        return getTypeByClassName(clazz.getName());
    }

    /**
     * @return TypeDef or null if no typeDef found
     */
    public TypeDef getTypeByClassName(String name)
    {
        for (TypeDef t : this.types.values())
        {
            if (t.getClassName().equals(name))
                return t;
        }
        return null;
    }

    public Collection<TypeDef> getTypes()
    {
        return this.types.values();
    }

    public void setTypes(Map<String, TypeDef> types)
    {
        this.types = types;
    }

    public Collection<TypeDef> getTypesBySuperClass(Class<?> superClass)
    {
        try
        {
            // FIXME Is there an alternative to instatiating classes here?
            List<TypeDef> matches = new ArrayList<TypeDef>();
            for (TypeDef t : this.types.values())
            {
                Class<?> cls = Class.forName(t.getSuperClassName());
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
