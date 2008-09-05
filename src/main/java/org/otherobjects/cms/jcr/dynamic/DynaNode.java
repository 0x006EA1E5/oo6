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
 * FIXME Needs to support Groovy/Freemarker property access eg object.property="value";
 * 
 * @author rich
 */
public class DynaNode extends BaseNode
{
    public static final String DYNA_NODE_DATAMAP_NAME = "data";

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

    /*
    @Override
    public Object get(String propertyName)
    {
        return data.get(propertyName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(String propertyName, Object value)
    {
        data.put(propertyName, value);
    }
    */

    @Override
    public String getOoType()
    {
        return ooType;
    }

    @Override
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
        return (String) (data.get("code") != null ? data.get("code") : StringUtils.generateUrlCode(getLabel()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setCode(String code)
    {
        data.put("code", code);
    }
    
    
}
