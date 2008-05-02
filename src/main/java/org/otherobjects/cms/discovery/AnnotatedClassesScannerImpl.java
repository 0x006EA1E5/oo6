package org.otherobjects.cms.discovery;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.otherobjects.cms.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * Thin wrapper around Spring's {@link ClassPathScanningCandidateComponentProvider} that 'abuses' it to find the names of classes bearing arbitrary
 * Annotations.
 * 
 * @author joerg
 *
 */
public class AnnotatedClassesScannerImpl implements AnnotatedClassesScanner
{

    public Set<String> findAnnotatedClasses(String[] packages, Class<? extends Annotation> annotation)
    {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false); //don't use default filters
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));

        Set<String> result = new HashSet<String>();

        for (String pkg : packages)
        {
            if (StringUtils.isNotBlank(pkg)) // ignore empty package names to avoid scanning the complete classpath
                result.addAll(extractClassNames(scanner.findCandidateComponents(pkg)));
        }

        return result;
    }

    private Set<String> extractClassNames(Set<BeanDefinition> beanDefinitions)
    {
        Set<String> result = new HashSet<String>();
        for (BeanDefinition def : beanDefinitions)
        {
            result.add(def.getBeanClassName());
        }
        return result;
    }

}
