package org.otherobjects.cms.types;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Maintain register of all data types definitions (TypeDef) in the system.
 * 
 * <p>Currently only supports JCR-backed types.
 * 
 * <p>IDEA Could we register via annotations too?
 * 
 * @author rich
 */
public class TypeService
{
    private Map<String, TypeDef> types = new LinkedHashMap<String, TypeDef>();

    public TypeService()
    {
        registerFundamentalTypes();
    }
    
    private void registerFundamentalTypes()
    {
        TypeDef td = new TypeDef("oo_TypeDef");
        td.addProperty(new PropertyDef("name","string",null));
        registerType(td);
        
        TypeDef pd = new TypeDef("oo_PropertyDef");
        pd.addProperty(new PropertyDef("name","string",null));
        pd.addProperty(new PropertyDef("type","string",null));
        pd.addProperty(new PropertyDef("relatedType","string",null));
        registerType(pd);
    }

    /**
     * 
     * @param t
     */
    public void registerType(TypeDef t)
    {
        types.put(t.getName(),t);
    }
    
    public TypeDef getType(String name)
    {
        return types.get(name);
    }
    
    public Collection<TypeDef> getTypes()
    {
        return (Collection<TypeDef>) types.values();
    }
    
    /*
    public Collection<TypeDef> getExternalTypes()
    {
        Collection<TypeDef> t = new ArrayList<TypeDef>();
        for(TypeDef td : (Collection<TypeDef>) types.values())
        {
            if(true)
                t.add(td);
        }
        
        return t;
    }
    */
}
