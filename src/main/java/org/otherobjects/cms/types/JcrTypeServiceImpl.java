package org.otherobjects.cms.types;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jackrabbit.ocm.manager.atomictypeconverter.AtomicTypeConverter;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.BooleanTypeConverterImpl;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.Date2LongTypeConverterImpl;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.LongTypeConverterImpl;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.StringTypeConverterImpl;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.beans.JcrBeanService;
import org.otherobjects.cms.jcr.BigDecimalTypeConverterImpl;

public class JcrTypeServiceImpl extends AbstactTypeService
{
    private Map<String, AtomicTypeConverter> jcrAtomicConverters;
    private Map<String, Class<?>> jcrClassMappings;

    private TypeDefDao typeDefDao;
    private JcrBeanService jcrBeanService;

    public void init()
    {
        reset();
        loadTypes();
        generateClasses();
    }

    /**
     * Loads TypeDefs from JCR. Types are stored in the default workspace at /types.
     */
    private void loadTypes()
    {
        List<TypeDef> typeDefs = typeDefDao.getAll();
        for (TypeDef t : typeDefs)
        {
            registerType(t);
        }
    }

    /**
     * Generates classes for types not backed by an existing class.
     */
    public void generateClasses()
    {
        for (TypeDef t : getTypes())
        {
            if (!t.hasClass())
            {
                // Create bean class
                t.setClassName(jcrBeanService.createCustomDynaNodeClass(t));
            }
        }
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

    public Map<String, Class<?>> getJcrClassMappings()
    {
        return jcrClassMappings;
    }

    public void setJcrClassMappings(Map<String, Class<?>> jcrClassMappings)
    {
        this.jcrClassMappings = jcrClassMappings;
    }

    public void reset()
    {
        registerConverters();
        registerClassMappings();
    }

    public String getClassNameForType(String type)
    {
        return jcrClassMappings.get(type).getName();
    }

    private void registerConverters()
    {
        jcrAtomicConverters = new HashMap<String, AtomicTypeConverter>();
        jcrAtomicConverters.put("string", new StringTypeConverterImpl());
        jcrAtomicConverters.put("text", new StringTypeConverterImpl());
        jcrAtomicConverters.put("html", new StringTypeConverterImpl());
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
        jcrClassMappings.put("html", String.class);
        jcrClassMappings.put("date", Date.class);
        jcrClassMappings.put("time", Date.class);
        jcrClassMappings.put("timestamp", Date.class);
        jcrClassMappings.put("boolean", Boolean.class);
        jcrClassMappings.put("number", Long.class);
        jcrClassMappings.put("decimal", BigDecimal.class);
    }

    public TypeDefDao getTypeDefDao()
    {
        return typeDefDao;
    }

    public void setTypeDefDao(TypeDefDao typeDefDao)
    {
        this.typeDefDao = typeDefDao;
    }

    public void setJcrBeanService(JcrBeanService jcrBeanService)
    {
        this.jcrBeanService = jcrBeanService;
    }
}
