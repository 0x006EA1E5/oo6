package org.otherobjects.cms.types;

import java.math.BigDecimal;
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
import org.otherobjects.cms.jcr.CmsNodeService;

public class JcrTypeServiceImpl extends AbstactTypeService
{
    private Map<String, AtomicTypeConverter> jcrAtomicConverters;
    private Map<String, Class<?>> jcrClassMappings;

    private CmsNodeService cmsNodeService;

    public JcrTypeServiceImpl()
    {
        reset();
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
        registerFundamentalTypes();
        registerConverters();
        registerClassMappings();
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
        setTypes(new LinkedHashMap<String, TypeDef>());
        
        TypeDef t = new TypeDef("oo_TypeDef");
        t.addProperty(new PropertyDef("name", "string", null, null));
        registerType(t);

        TypeDef pd = new TypeDef("oo_PropertyDef");
        pd.addProperty(new PropertyDef("name", "string", null, null));
        pd.addProperty(new PropertyDef("type", "string", null, null));
        pd.addProperty(new PropertyDef("relatedType", "string", null, null));
        registerType(pd);

        TypeDef td = new TypeDef("org.otherobjects.cms.jcr.TestObject");
        td.addProperty(new PropertyDef("testString", "string", null, null));
        td.addProperty(new PropertyDef("testText", "text", null, null));
        td.addProperty(new PropertyDef("testDate", "date", null, null));
        td.addProperty(new PropertyDef("testTime", "time", null, null));
        td.addProperty(new PropertyDef("testTimestamp", "timestamp", null, null));
        td.addProperty(new PropertyDef("testNumber", "number", null, null));
        td.addProperty(new PropertyDef("testDecimal", "decimal", null, null));
        td.addProperty(new PropertyDef("testBoolean", "boolean", null, null));
        td.addProperty(new PropertyDef("testReference", "reference", "org.otherobjects.cms.jcr.TestReferenceObject", null));
        td.addProperty(new PropertyDef("testComponent", "component", "org.otherobjects.cms.jcr.TestComponentObject", null));
        td.addProperty(new PropertyDef("testStringsList", "string", null, "list"));
        td.addProperty(new PropertyDef("testComponentsList", "component", null, "list"));
        td.addProperty(new PropertyDef("testReferencesList", "reference", null, "list"));
        registerType(td);

        TypeDef td2 = new TypeDef("org.otherobjects.cms.jcr.TestReferenceObject");
        td2.addProperty(new PropertyDef("name", "string", null, null));
        registerType(td2);

        TypeDef td3 = new TypeDef("org.otherobjects.cms.jcr.TestComponentObject");
        td3.addProperty(new PropertyDef("name", "string", null, null));
        td3.addProperty(new PropertyDef("component", "component", "org.otherobjects.cms.jcr.TestComponentObject", null));
        registerType(td3);

    }

    public CmsNodeService getCmsNodeService()
    {
        return cmsNodeService;
    }

    public void setCmsNodeService(CmsNodeService cmsNodeService)
    {
        this.cmsNodeService = cmsNodeService;
    }
}
