package org.otherobjects.cms.tools;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.views.Tool;

/**
 * Allows dynamic access to bean properties.
 * 
 * TODO Use this universally?
 * 
 * @author rich
 */
@Tool("beanTool")
public class BeanTool
{
    public Object getPropertyValue(Object bean, String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return PropertyUtils.getProperty(bean, propertyName);
    }

    public String stringValue(Object bean)
    {
        return String.valueOf(bean);
    }
}
