package org.otherobjects.cms.dao;

import java.util.Comparator;

import org.apache.commons.beanutils.PropertyUtils;

@SuppressWarnings("unchecked")
public class BeanPropertyComparator implements Comparator
{

    private String propertyName;

    public BeanPropertyComparator(String propertyName)
    {
        this.propertyName = propertyName;
    }

    /*
     * 
     * (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object obj1, Object obj2)
    {
        try
        {
            Object lhsProperty = PropertyUtils.getNestedProperty(obj1, propertyName);
            Object rhsProperty = PropertyUtils.getNestedProperty(obj2, propertyName);

            if (!Comparable.class.isAssignableFrom(lhsProperty.getClass()) || !Comparable.class.isAssignableFrom(rhsProperty.getClass()))
                throw new RuntimeException(lhsProperty.getClass().getName() + " is not Comparable");

            return ((Comparable) lhsProperty).compareTo(rhsProperty);

        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Specified property is not Comparable or can't be read from bean", e);
        }
    }

}
