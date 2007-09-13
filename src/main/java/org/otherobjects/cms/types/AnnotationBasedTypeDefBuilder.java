package org.otherobjects.cms.types;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.types.annotation.PropertyDefAnnotation;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.TypeDefAnnotation;
import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.AnnotationUtils;

@SuppressWarnings("unchecked")
public class AnnotationBasedTypeDefBuilder implements TypeDefBuilder
{

    public TypeDef getTypeDef(String type) throws Exception
    {
        return getTypeDef(Class.forName(type));
    }

    public TypeDef getTypeDef(Class clazz) throws Exception
    {
        if (!clazz.isAnnotationPresent(TypeDefAnnotation.class))
            throw new OtherObjectsException("TypeDef can't be build as there are no annotations present on type " + clazz.getName());

        TypeDefAnnotation typeDefAnnotation = (TypeDefAnnotation) clazz.getAnnotation(TypeDefAnnotation.class);
        TypeDef typeDef = new TypeDef();
        typeDef.setClassName(clazz.getName());
        typeDef.setSuperClassName(typeDefAnnotation.superClassName());
        typeDef.setJcrPath(typeDefAnnotation.jcrPath());
        typeDef.setLabel(typeDefAnnotation.label());
        typeDef.setDescription(typeDefAnnotation.description());
        typeDef.setLabelProperty(typeDefAnnotation.labelProperty());
        
        //Create a list as a container to allow for ordered addition of found PropertyDefs
        List<PropertyDef> propDefs = new ArrayList<PropertyDef>(); 
        
        // iterate all public methods (including inherited ones)
        Method[] methods = clazz.getMethods();
        for (Method method : methods)
        {
            PropertyDefAnnotation propertyDefAnnotation = AnnotationUtils.getAnnotation(method, PropertyDefAnnotation.class);
            if (propertyDefAnnotation != null)
            {
                PropertyDef propertyDef = new PropertyDef();
                propertyDef.setName(getPropertyNameFromGetterOrSetter(method.getName()));
                propertyDef.setDescription(propertyDefAnnotation.description());
                propertyDef.setHelp(propertyDefAnnotation.help());
                propertyDef.setLabel(propertyDefAnnotation.label());
                propertyDef.setParentTypeDef(typeDef);
                propertyDef.setRequired(propertyDefAnnotation.required());
                propertyDef.setSize(propertyDefAnnotation.size());
                propertyDef.setType(propertyDefAnnotation.type().value());
                propertyDef.setValang(propertyDefAnnotation.valang());
                propertyDef.setOrder(propertyDefAnnotation.order());
                // TODO Reference and component support
                if (propertyDefAnnotation.type().equals(PropertyType.LIST))
                {
                    propertyDef.setRelatedType(propertyDefAnnotation.relatedType());
                    propertyDef.setCollectionElementType(propertyDefAnnotation.collectionElementType().value());
                }
                propDefs.add(propertyDef);
            }
        }
        
        Collections.sort(propDefs, new OrderComparator());
        
        for(PropertyDef orderedPropertyDef : propDefs)
        {
        	typeDef.addProperty(orderedPropertyDef);
        }

        return typeDef;
    }

    static Pattern pattern = Pattern.compile("(?:(?:s|get)|(?:is))(\\w{1})(.*)");

    //FIXME clearly somebody else must have done this - and maybe in a cleverer way
    private String getPropertyNameFromGetterOrSetter(String methodName)
    {
        Matcher matcher = pattern.matcher(methodName);
        if (matcher.matches())
            return matcher.group(1).toLowerCase() + matcher.group(2);
        else
            throw new OtherObjectsException("The annotated method " + methodName + " doesn't seem to follow bean style conventions");
    }

}
