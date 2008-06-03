package org.otherobjects.cms.jcr.dynamic;

import org.apache.jackrabbit.ocm.manager.collectionconverter.impl.ManagedHashMap;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.util.StringUtils;
import org.springframework.util.Assert;

/**
 * Dynamic bean that can be stored in JCR. Valid data is defined by
 * its TypeDef.
 * 
 * TODO Need a way to add custom DAO methods?
 * FIXME Needs to support Groovy/Freemarker property access eg object.property="new";
 * 
 * @author rich
 */
public class DynaNode extends BaseNode //implements Map
{
    private ManagedHashMap data = new ManagedHashMap();
    private String ooType;

    public DynaNode()
    {
        // FIXME Only JCR OCM internals should call this
    }

    /**
     * DynaNodes must always have a defining TypeDef. The constructor takes the typeDef name which is then
     * looked up in the TypeService to ensure that it exists. 
     * 
     * @param typeDef
     */
    public DynaNode(String ooType)
    {
        setOoType(ooType);
        Assert.notNull(getTypeDef(), "No type definition found for: " + ooType);
    }

    /**
     * Protected constructor. Mainly used in test cases.
     * @param typeDef
     */
    protected DynaNode(TypeDef typeDef)
    {
        setTypeDef(typeDef);
        setOoType(typeDef.getName());
    }
    
    public Object get(String propertyName)
    {
        return data.get(propertyName);
    }

    @SuppressWarnings("unchecked")
    public void set(String propertyName, Object value)
    {
        data.put(propertyName, value);
    }

    public String getOoType()
    {
        return ooType;
    }

    public void setOoType(String ooType)
    {
        this.ooType = ooType;
    }

    @Override
    public void setTypeDef(TypeDef typeDef)
    {
        super.setTypeDef(typeDef);
        setOoType(typeDef.getName());
    }

    public ManagedHashMap getData()
    {
        return data;
    }

    public void setData(ManagedHashMap data)
    {
        this.data = data;
    }

    @Override
    public String getCode()
    {
        // FIXME Merge this with BaseNode code?
        return (String) (get("code") != null ? get("code") : StringUtils.generateUrlCode(getLabel()));
    }
    
    @Override
    public void setCode(String code)
    {
        set("code", code);
    }
    
//    /*
//     * FIXME Map interface methods. If these stay they should include standard node fields.
//     */
//    public void clear()
//    {
//        data.clear();
//    }
//
//    public boolean containsKey(Object key)
//    {
//        return data.containsKey(key);
//    }
//
//    public boolean containsValue(Object value)
//    {
//        return data.containsValue(value);
//    }
//
//    public Set entrySet()
//    {
//        return data.entrySet();
//    }
//
//    public Object get(Object key)
//    {
//        return data.get(key);
//    }
//
//    public boolean isEmpty()
//    {
//        return data.isEmpty();
//    }
//
//    public Set keySet()
//    {
//        return data.keySet();
//    }
//
//    public Object put(Object key, Object value)
//    {
//        if(key.equals("code")) setCode((String) value);
//        
//        return data.put(key, value);
//    }
//
//    public void putAll(Map t)
//    {
//        data.putAll(t);
//    }
//
//    public Object remove(Object key)
//    {
//        return data.remove(key);
//    }
//
//    public int size()
//    {
//        return data.size();
//    }
//
//    public Collection values()
//    {
//        return data.values();
//    }

}
