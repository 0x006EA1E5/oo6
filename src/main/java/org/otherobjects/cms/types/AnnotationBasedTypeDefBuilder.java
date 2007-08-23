package org.otherobjects.cms.types;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.types.annotation.PropertyDefAnnotation;
import org.otherobjects.cms.types.annotation.TypeDefAnnotation;
import org.springframework.core.annotation.AnnotationUtils;


public class AnnotationBasedTypeDefBuilder implements TypeDefBuilder {
	

	public TypeDef getTypeDef(String type) throws Exception
	{
		return getTypeDef(Class.forName(type));
	}

	public TypeDef getTypeDef(Class clazz) throws Exception {
		
		if(!clazz.isAnnotationPresent(TypeDefAnnotation.class))
			throw new OtherObjectsException("TypeDef can't be build as there are no annotations present on type " + clazz.getName());
		
		TypeDefAnnotation typeDefAnnotation = (TypeDefAnnotation) clazz.getAnnotation(TypeDefAnnotation.class);
		TypeDef typeDef = new TypeDef();
		typeDef.setClassName(clazz.getName());
		typeDef.setSuperClassName(typeDefAnnotation.superClassName());
		typeDef.setJcrPath(typeDefAnnotation.jcrPath());
		typeDef.setDescription(typeDefAnnotation.description());
		typeDef.setLabelProperty(typeDefAnnotation.labelProperty());
		
		// iterate all public methods (including inherited ones)
		Method[] methods = clazz.getMethods();
		for(int i = 0; i < methods.length; i++)
		{
			Method method = methods[i];
			PropertyDefAnnotation propertyDefAnnotation = AnnotationUtils.getAnnotation(method, PropertyDefAnnotation.class);
			if( propertyDefAnnotation != null)
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
				typeDef.addProperty(propertyDef);
			}
		}
		
		return typeDef;
	}
	
	static Pattern pattern = Pattern.compile("(?:(?:s|get)|(?:is))(\\w{1})(.*)");
	
	//FIXME clearly somebody else must have done this - and maybe in a cleverer way
	private String getPropertyNameFromGetterOrSetter(String methodName)
	{
		Matcher matcher = pattern.matcher(methodName);
		if(matcher.matches())
		{
			return matcher.group(1).toLowerCase() + matcher.group(2);
		}
		else
		{
			throw new OtherObjectsException("The annotated method " + methodName + " doesn't seem to follow bean style conventions");
		}
	}
	
	
	
}
