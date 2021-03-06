package org.otherobjects.cms.types;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.model.BaseComponent;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;
import org.otherobjects.framework.OtherObjectsException;
import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * TODO Allow method annotations as well an field annotations (see Hibernate)
 * 
 * @author rich
 */
@Component
public class AnnotationBasedTypeDefBuilder implements TypeDefBuilder, InitializingBean
{
    @Resource
    private OtherObjectsConfigurator otherObjectsConfigurator;

    public TypeDef getTypeDef(String type) throws Exception
    {
        return getTypeDef(Class.forName(type));
    }

    private void initialisePropertyFormats()
    {
        Assert.notNull(otherObjectsConfigurator, "No configurator available to set data formats.");
        PropertyDefImpl.setDateFormat(otherObjectsConfigurator.getProperty("otherobjects.default.date.format"));
        PropertyDefImpl.setTimeFormat(otherObjectsConfigurator.getProperty("otherobjects.default.time.format"));
        PropertyDefImpl.setTimestampFormat(otherObjectsConfigurator.getProperty("otherobjects.default.timestamp.format"));
    }

    public TypeDef getTypeDef(Class<?> clazz) throws Exception
    {
        if (!clazz.isAnnotationPresent(Type.class))
            return null;

        Type typeDefAnnotation = clazz.getAnnotation(Type.class);

        TypeDefImpl typeDef = new TypeDefImpl();

        if (BaseComponent.class.isAssignableFrom(clazz))
            typeDef.setComponent(true);

        typeDef.setName(clazz.getName());
        typeDef.setClassName(clazz.getName());
        typeDef.setSuperClassName(typeDefAnnotation.superClassName());
        typeDef.setStore(typeDefAnnotation.store());
        typeDef.setImageProperty(typeDefAnnotation.imageProperty());
        if (StringUtils.isNotBlank(typeDefAnnotation.adminControllerUrl()))
            typeDef.setCustomAdminController(typeDefAnnotation.adminControllerUrl());

        typeDef.setLabel(typeDefAnnotation.label());
        // Infer label if not set
        if (StringUtils.isEmpty(typeDefAnnotation.label()))
        {
            typeDef.setLabel(org.otherobjects.cms.util.StringUtils.generateLabel(typeDefAnnotation.label()));
        }
        else
        {
            typeDef.setLabel(typeDefAnnotation.label());
        }

        typeDef.setDescription(typeDefAnnotation.description());
        typeDef.setLabelProperty(typeDefAnnotation.labelProperty());
        typeDef.setCodeProperty(typeDefAnnotation.codeProperty());

        // Create a list as a container to allow for ordered addition of found PropertyDefs
        List<PropertyDef> propDefs = new ArrayList<PropertyDef>();

        // Iterate all fields
        Method[] methods = clazz.getMethods();
        for (Method method : methods)
        {
            // Property propertyDefAnnotation = AnnotationUtils.getAnnotation(field, Property.class);
            Property propertyDefAnnotation = method.getAnnotation(Property.class);
            if (propertyDefAnnotation != null)
            {
                PropertyDefImpl propertyDef = new PropertyDefImpl();
                propertyDef.setName(getPropertyNameFromGetterOrSetter(method.getName()));
                propertyDef.setDescription(propertyDefAnnotation.description());
                propertyDef.setHelp(propertyDefAnnotation.help());

                // Infer type if not set
                if (propertyDefAnnotation.type().equals(PropertyType.UNDEFINED))
                {
                    PropertyType pt = getDefaultTypeForClass(method.getReturnType());
                    Assert.notNull(pt, "No default type can be inferred for property: " + method.getName());
                    propertyDef.setType(pt.value());
                }
                else
                {
                    propertyDef.setType(propertyDefAnnotation.type().value());
                }

                if (StringUtils.isEmpty(propertyDefAnnotation.label()))
                {
                    propertyDef.setLabel(propertyDefAnnotation.label());
                }

                propertyDef.setParentTypeDef(typeDef);
                propertyDef.setRequired(propertyDefAnnotation.required());
                propertyDef.setSize(propertyDefAnnotation.size());
                propertyDef.setValang(propertyDefAnnotation.valang());
                propertyDef.setOrder(propertyDefAnnotation.order());
                propertyDef.setFieldType(StringUtils.isEmpty(propertyDefAnnotation.fieldType()) ? null : propertyDefAnnotation.fieldType());
                if (propertyDef.getType().equals(PropertyType.COMPONENT.value()) || propertyDef.getType().equals(PropertyType.REFERENCE.value()))
                {
                    // If not specified infer related type for components and references
                    Class<?> relatedType;
                    if (StringUtils.isEmpty(propertyDefAnnotation.relatedType()))
                    {
                        relatedType = method.getReturnType();
                    }
                    else
                    {
                        relatedType = Class.forName(propertyDefAnnotation.relatedType());
                    }
                    propertyDef.setRelatedType(relatedType.getName());
                }

                if (propertyDef.getType().equals(PropertyType.LIST.value()))
                {
                    // Get generic return type
                    Class<?> listTypeClass = null;
                    ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
                    if (genericReturnType != null)
                        listTypeClass = (Class<?>) genericReturnType.getActualTypeArguments()[0];

                    // Infer collection type from list parametized type
                    if (propertyDefAnnotation.collectionElementType().equals(PropertyType.UNDEFINED))
                    {
                        Assert.notNull(listTypeClass, "Could not infer collectionElementType (List is a raw type) for: " + method.getName());
                        PropertyType collectionElementType = getDefaultTypeForClass(listTypeClass);
                        Assert.notNull(collectionElementType, "Could not infer collectionElementType (unknown List type) for: " + method.getName());
                        propertyDef.setCollectionElementType(collectionElementType.value());
                    }
                    else
                    {
                        propertyDef.setCollectionElementType(propertyDefAnnotation.collectionElementType().value());
                    }

                    // If collection type is reference or list then infer relatedType
                    if (propertyDef.getCollectionElementType().equals(PropertyType.COMPONENT.value()) || propertyDef.getCollectionElementType().equals(PropertyType.REFERENCE.value()))
                    {
                        // Infer collection type from list parametized type
                        if (StringUtils.isEmpty(propertyDefAnnotation.relatedType()))
                        {
                            Assert.notNull(listTypeClass, "Could not infer collectionElementType (List is a raw type) for: " + method.getName());
                            propertyDef.setRelatedType(listTypeClass.getName());
                        }
                        else
                        {
                            propertyDef.setRelatedType(propertyDefAnnotation.relatedType());
                        }
                    }
                }
                //                if (propertyDef.getType().equals(PropertyType.MAP.value()))
                //                {
                //                    // Get generic return type
                //                    Class<?> mapTypeClass = null;
                //                    ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
                //                    if (genericReturnType != null)
                //                        mapTypeClass = (Class<?>) genericReturnType.getActualTypeArguments()[0];
                //
                //                    // Infer collection type from map parametized type
                //                    if (propertyDefAnnotation.collectionElementType().equals(PropertyType.UNDEFINED))
                //                    {
                //                        Assert.notNull(mapTypeClass, "Could not infer collectionElementType (Map is a raw type) for: " + method.getName());
                //                        PropertyType collectionElementType = getDefaultTypeForClass(mapTypeClass);
                //                        Assert.notNull(collectionElementType, "Could not infer collectionElementType (unknown List type) for: " + method.getName());
                //                        propertyDef.setCollectionElementType(collectionElementType.value());
                //                    }
                //                    else
                //                    {
                //                        propertyDef.setCollectionElementType(propertyDefAnnotation.collectionElementType().value());
                //                    }
                //
                //                    // If collection type is reference or list then infer relatedType
                //                    if (propertyDef.getCollectionElementType().equals(PropertyType.COMPONENT.value()) || propertyDef.getCollectionElementType().equals(PropertyType.REFERENCE.value()))
                //                    {
                //                        // Infer collection type from list parametized type
                //                        if (StringUtils.isEmpty(propertyDefAnnotation.relatedType()))
                //                        {
                //                            Assert.notNull(mapTypeClass, "Could not infer collectionElementType (Map is a raw type) for: " + method.getName());
                //                            propertyDef.setRelatedType(mapTypeClass.getName());
                //                        }
                //                        else
                //                        {
                //                            propertyDef.setRelatedType(propertyDefAnnotation.relatedType());
                //                        }
                //                    }
                //
                //                }
                propDefs.add(propertyDef);
            }
        }

        Collections.sort(propDefs, new OrderComparator());

        for (PropertyDef orderedPropertyDef : propDefs)
        {
            typeDef.addProperty(orderedPropertyDef);
        }

        return typeDef;
    }

    private PropertyType getDefaultTypeForClass(Class<?> type)
    {
        if (type.equals(String.class))
            return PropertyType.STRING;

        else if (type.equals(Boolean.class))
            return PropertyType.BOOLEAN;

        else if (type.equals(Date.class))
            return PropertyType.DATE;

        else if (type.equals(Long.class))
            return PropertyType.NUMBER;

        else if (type.equals(BigDecimal.class))
            return PropertyType.DECIMAL;

        else if (BaseNode.class.isAssignableFrom(type))
            return PropertyType.REFERENCE;

        else if (List.class.isAssignableFrom(type))
            return PropertyType.LIST;

        //        else if (Map.class.isAssignableFrom(type))
        //            return PropertyType.MAP;

        else if (OoResource.class.isAssignableFrom(type))
            return PropertyType.OORESOURCE;
        // No suitable default available
        return null;
    }

    private static final Pattern PATTERN = Pattern.compile("(?:(?:is|get)|(?:is))(\\w{1})(.*)");

    //FIXME clearly somebody else must have done this - and maybe in a cleverer way
    private String getPropertyNameFromGetterOrSetter(String methodName)
    {
        Matcher matcher = PATTERN.matcher(methodName);
        if (matcher.matches())
            return matcher.group(1).toLowerCase() + matcher.group(2);
        else
            throw new OtherObjectsException("The annotated method " + methodName + " doesn't seem to follow bean style conventions");
    }

    public void setOtherObjectsConfigurator(OtherObjectsConfigurator otherObjectsConfigurator)
    {
        this.otherObjectsConfigurator = otherObjectsConfigurator;
    }

    public void afterPropertiesSet() throws Exception
    {
        initialisePropertyFormats();
    }
}
