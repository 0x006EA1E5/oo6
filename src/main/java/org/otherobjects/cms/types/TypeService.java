package org.otherobjects.cms.types;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.jackrabbit.ocm.persistence.atomictypeconverter.AtomicTypeConverter;
import org.apache.jackrabbit.ocm.persistence.atomictypeconverter.impl.BooleanTypeConverterImpl;
import org.apache.jackrabbit.ocm.persistence.atomictypeconverter.impl.Date2LongTypeConverterImpl;
import org.apache.jackrabbit.ocm.persistence.atomictypeconverter.impl.LongTypeConverterImpl;
import org.apache.jackrabbit.ocm.persistence.atomictypeconverter.impl.StringTypeConverterImpl;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.jcr.BigDecimalTypeConverterImpl;

/**
 * Maintain register of all data types definitions (TypeDef) in the system.
 * 
 * <p>Currently only supports JCR-backed types.
 * 
 * <p>IDEA Could we register via annotations too?
 * 
 * @author rich
 */
public class TypeService
{
    private Map<String, AtomicTypeConverter> jcrAtomicConverters;
    private Map<String, Class<?>> jcrClassMappings;
    private Map<String, TypeDef> types;

    private static TypeService instance;

    private TypeService()
    {
    }

    public static TypeService getInstance()
    {
        if (instance == null)
        {
            instance = new TypeService();
            instance.reset();
        }
        return instance;
    }

    private void registerConverters()
    {
        jcrAtomicConverters = new HashMap<String, AtomicTypeConverter>();
        jcrAtomicConverters.put("string", new StringTypeConverterImpl());
        jcrAtomicConverters.put("text", new StringTypeConverterImpl());
        jcrAtomicConverters.put("date", new Date2LongTypeConverterImpl());
        jcrAtomicConverters.put("time", new Date2LongTypeConverterImpl());
        jcrAtomicConverters.put("timestamp", new Date2LongTypeConverterImpl());
        jcrAtomicConverters.put("boolean", new BooleanTypeConverterImpl());
        jcrAtomicConverters.put("number", new LongTypeConverterImpl());
        jcrAtomicConverters.put("decimal", new BigDecimalTypeConverterImpl());
    }
    
    private void registerClassMappings()
    {
        jcrClassMappings = new HashMap<String, Class<?>>();
        jcrClassMappings.put("string", String.class);
        jcrClassMappings.put("text", String.class);
        jcrClassMappings.put("date", Date.class);
        jcrClassMappings.put("time", Date.class);
        jcrClassMappings.put("timestamp", Date.class);
        jcrClassMappings.put("boolean", Boolean.class);
        jcrClassMappings.put("number", Long.class);
        jcrClassMappings.put("decimal", BigDecimal.class);
    }

    private void registerFundamentalTypes()
    {
        types = new LinkedHashMap<String, TypeDef>();
        TypeDef td = new TypeDef("oo_TypeDef");
        td.addProperty(new PropertyDef("name", "string", null, null));
        registerType(td);

        TypeDef pd = new TypeDef("oo_PropertyDef");
        pd.addProperty(new PropertyDef("name", "string", null, null));
        pd.addProperty(new PropertyDef("type", "string", null, null));
        pd.addProperty(new PropertyDef("relatedType", "string", null, null));
        registerType(pd);
    }

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

    public Object getJcrConverter(String type)
    {
        AtomicTypeConverter atomicTypeConverter = getJcrAtomicConverters().get(type);
        if (atomicTypeConverter == null)
            throw new OtherObjectsException("No JCR converter defined for type: " + type);
        return atomicTypeConverter;
    }
    
    public Class<?> getJcrClassMapping(String type)
    {
        Class<?> clazz = getJcrClassMappings().get(type);
        if (clazz == null)
            throw new OtherObjectsException("No JCR class defined for type: " + type);
        return clazz;
    }

    public Map<String, AtomicTypeConverter> getJcrAtomicConverters()
    {
        return jcrAtomicConverters;
    }

    public void setJcrAtomicConverters(Map<String, AtomicTypeConverter> jcrAtomicConverters)
    {
        this.jcrAtomicConverters = jcrAtomicConverters;
    }

    public void reset()
    {
        instance.registerFundamentalTypes();
        instance.registerConverters();
        instance.registerClassMappings();
    }

    public Map<String, Class<?>> getJcrClassMappings()
    {
        return jcrClassMappings;
    }

    public void setJcrClassMappings(Map<String, Class<?>> jcrClassMappings)
    {
        this.jcrClassMappings = jcrClassMappings;
    }

    /*
    public Collection<TypeDef> getExternalTypes()
    {
        Collection<TypeDef> t = new ArrayList<TypeDef>();
        for(TypeDef td : (Collection<TypeDef>) types.values())
        {
            if(true)
                t.add(td);
        }
        
        return t;
    }
    */
}
