package org.otherobjects.cms.types;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jackrabbit.ocm.manager.atomictypeconverter.AtomicTypeConverter;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.BooleanTypeConverterImpl;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.Date2LongTypeConverterImpl;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.LongTypeConverterImpl;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.StringTypeConverterImpl;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.beans.JcrBeanService;
import org.otherobjects.cms.jcr.BigDecimalTypeConverterImpl;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.JcrTypeDef;

public class TypeServiceImpl extends AbstractTypeService
{
    private Map<String, AtomicTypeConverter> jcrAtomicConverters;
    private Map<String, Class<?>> jcrClassMappings;

    private JcrBeanService jcrBeanService;
    private AnnotationBasedTypeDefBuilder annotationBasedTypeDefBuilder;

    @SuppressWarnings("unchecked")
    public void init()
    {
        reset();

        // FIXME Temp hack to manually load annotated types
        try
        {
            Set<Class<?>> annotatedClasses = annotationBasedTypeDefBuilder.findAnnotatedClasses("org.otherobjects.cms.model");
            for (Class c : annotatedClasses)
                registerType(annotationBasedTypeDefBuilder.getTypeDef(c));

            annotatedClasses = annotationBasedTypeDefBuilder.findAnnotatedClasses("com.example.site.model");
            for (Class c : annotatedClasses)
                registerType(annotationBasedTypeDefBuilder.getTypeDef(c));

            // FIXME Add TypeDef Validator here: labelProperty, extends BaseNode etc
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Error loading annotated classes.", e);
        }

        generateClasses();
    }

    @Override
    public TypeDef getType(String name)
    {
        TypeDef type = super.getType(name);
        return type;
    }

    /**
     * Loads TypeDefs from JCR. Types are stored in the default workspace at /types.
     * 
     * Called from BootstrapUtils.
     */
    public void loadJcrBackedTypes(UniversalJcrDao universalJcrDao)
    {
        // FIXME Change to getAllByType
        List<BaseNode> typeDefs = universalJcrDao.getAllByPath("/types/");
        for (BaseNode t : typeDefs)
        {
            TypeDef t2 = ((JcrTypeDef) t).toTypeDef();
            t2.setTypeService(this);
            registerType(t2);
        }
        generateClasses();
    }

    /**
     * Generates classes for types not backed by an existing class.
     */
    public void generateClasses()
    {
        for (TypeDef t : getTypes())
        {
            generateClass(t);
        }
    }

    public void generateClass(TypeDef t)
    {
        if (!t.hasClass())
        {
            // Create bean class
            t.setClassName(this.jcrBeanService.createCustomDynaNodeClass(t));;
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
        return this.jcrAtomicConverters;
    }

    public void setJcrAtomicConverters(Map<String, AtomicTypeConverter> jcrAtomicConverters)
    {
        this.jcrAtomicConverters = jcrAtomicConverters;
    }

    public Map<String, Class<?>> getJcrClassMappings()
    {
        return this.jcrClassMappings;
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
        return this.jcrClassMappings.get(type).getName();
    }

    private void registerConverters()
    {
        this.jcrAtomicConverters = new HashMap<String, AtomicTypeConverter>();
        this.jcrAtomicConverters.put("string", new StringTypeConverterImpl());
        this.jcrAtomicConverters.put("text", new StringTypeConverterImpl());
        this.jcrAtomicConverters.put("html", new StringTypeConverterImpl());
        this.jcrAtomicConverters.put("date", new Date2LongTypeConverterImpl());
        this.jcrAtomicConverters.put("time", new Date2LongTypeConverterImpl());
        this.jcrAtomicConverters.put("timestamp", new Date2LongTypeConverterImpl());
        this.jcrAtomicConverters.put("boolean", new BooleanTypeConverterImpl());
        this.jcrAtomicConverters.put("number", new LongTypeConverterImpl());
        this.jcrAtomicConverters.put("decimal", new BigDecimalTypeConverterImpl());
    }

    private void registerClassMappings()
    {
        this.jcrClassMappings = new HashMap<String, Class<?>>();
        this.jcrClassMappings.put("string", String.class);
        this.jcrClassMappings.put("text", String.class);
        this.jcrClassMappings.put("html", String.class);
        this.jcrClassMappings.put("date", Date.class);
        this.jcrClassMappings.put("time", Date.class);
        this.jcrClassMappings.put("timestamp", Date.class);
        this.jcrClassMappings.put("boolean", Boolean.class);
        this.jcrClassMappings.put("number", Long.class);
        this.jcrClassMappings.put("decimal", BigDecimal.class);
    }

    public void setJcrBeanService(JcrBeanService jcrBeanService)
    {
        this.jcrBeanService = jcrBeanService;
    }

    public void setAnnotationBasedTypeDefBuilder(AnnotationBasedTypeDefBuilder annotationBasedTypeDefBuilder)
    {
        this.annotationBasedTypeDefBuilder = annotationBasedTypeDefBuilder;
    }
}
