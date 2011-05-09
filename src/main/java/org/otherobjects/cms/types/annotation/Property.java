package org.otherobjects.cms.types.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.Ordered;
//FIXME need to add Maps to Property annotation
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Property 
{

    PropertyType type() default PropertyType.UNDEFINED;

    String fieldType() default "";

    String label() default "";

    String description() default "";

    boolean required() default false;

    int size() default -1;

    String valang() default "";

    String help() default "";

    PropertyType collectionElementType() default PropertyType.UNDEFINED;

    String relatedType() default "";

    int order() default Ordered.LOWEST_PRECEDENCE;

}
