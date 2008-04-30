package org.otherobjects.cms.discovery;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface AnnotatedClassesScanner
{
    /**
     * Finds all classes bearing the given annotation in the given list of base packages
     * 
     * @param packages
     * @param annotation
     * @return
     */
    public Set<String> findAnnotatedClasses(String[] packages, Class<? extends Annotation> annotation);
}
