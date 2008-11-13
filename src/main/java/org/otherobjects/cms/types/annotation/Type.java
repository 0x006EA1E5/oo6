package org.otherobjects.cms.types.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.otherobjects.cms.types.TypeDef;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Type
{

    String superClassName() default "org.otherobjects.cms.model.BaseNode";

    String label() default "";

    String description() default "";

    String labelProperty() default "code";
    
    String store() default TypeDef.JACKRABBIT;

}
