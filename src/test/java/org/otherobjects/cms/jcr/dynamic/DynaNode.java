package org.otherobjects.cms.jcr.dynamic;

import java.util.Map;

import org.otherobjects.cms.types.TypeDef;

/**
 * Dynamic bean that can be stored in JCR. Valid data is defined by
 * its TypeDef.
 * 
 * @author rich
 */
public class DynaNode
{
    private TypeDef typeDef;
    private Map<String, Object> data;
    
    /**
     * DynaNodes must always have a defining TypeDef.
     * 
     * @param typeDef
     */
    public DynaNode(TypeDef typeDef)
    {
        this.typeDef = typeDef;
    }
    
    public Object get(String propertyName)
    {
        return data.get(propertyName);
    }
    
    public void set(String propertyName, Object value)
    {
        data.put(propertyName, value);
    }

    public TypeDef getTypeDef()
    {
        return typeDef;
    }
}
