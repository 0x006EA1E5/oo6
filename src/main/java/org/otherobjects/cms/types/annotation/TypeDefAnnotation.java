package org.otherobjects.cms.types.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TypeDefAnnotation {
	
	String superClassName() default "java.lang.object";
	
	String description();
	
	String jcrPath();
	
	String labelProperty() default "label";
	
}
