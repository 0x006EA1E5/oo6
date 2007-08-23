package org.otherobjects.cms.types.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PropertyDefAnnotation {
	
	PropertyType type() default PropertyType.STRING;
	
	String label() default "";
	
	String description() default "";
	
	boolean required() default false;
	
	int size() default -1;
	
	String valang() default "";
	
	String help() default "";
	
	//String collectionElementType() default "";
	
	//String relatedType();
	
}
