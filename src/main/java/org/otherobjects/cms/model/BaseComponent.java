package org.otherobjects.cms.model;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.springframework.util.Assert;

/**
 * Document those property names that can't be used. Better still name space them?
 * 
 * FIXME Our standard props should be namespaced eg ooLabel?
 * 
 * @author rich
 */
public abstract class BaseComponent
{
    private String id;
    private String path;
    private String code;
    private TypeDef typeDef;


    public BaseComponent()
    {
    }
    
    public String getOoLabel()
    {
        
        return "label";
//        Assert.notNull(getLabelProperty(), "Could not get label since labelProperty is not set for: " + getOoType());
//        return String.valueOf(getPropertyValue(getLabelProperty()));
    }
    
    /**
     * Return the path of this node for use with JCR. The jcr path is comprised of
     * the path and the code.
     * 
     * @return the jcr path
     */
    public String getJcrPath()
    {
        // FIXME This means that this may not always be acurate. Make sure this ALWAYS returns true jcr path
        if (getPath() == null || getPath() == null)
            return null;

        Assert.isTrue(this.path.endsWith("/"), "Path must end with a forward slash");
        return getPath() + getCode();
    }

    /**
     * Sets the JCR path for this node. The path and code will be inferred form this.
     * 
     * @param jcrPath
     */
    public void setJcrPath(String jcrPath)
    {
        Assert.notNull(jcrPath, "jcrPath must not be null");

        Assert.isTrue(jcrPath.lastIndexOf("/") >= 0, "jcrPath must contain at least one forward slash");
        Assert.isTrue(!jcrPath.endsWith("/"), "jcrPath must not end with a forward slash");

        int slashPos = jcrPath.lastIndexOf("/");
        setPath(jcrPath.substring(0, slashPos + 1));
        setCode(jcrPath.substring(slashPos + 1));
    }
    
//    public Object getPropertyValue(String name)
//    {
//        // FIXME Need consistent method for accessing properties Spring vs BeanWrapper
//        try
//        {
//            return PropertyUtils.getProperty(this, name);
//        }
//        catch (Exception e)
//        {
//            throw new OtherObjectsException("Could not get property value for '" + name + "' in: " + getTypeDef().getName(), e);
//        }
//    }
//    
//    public String getLabelProperty()
//    {
//        Assert.notNull(getTypeDef(), "No TypeDef found for object: " + getClass().getName());
//        return getTypeDef().getLabelProperty();
//    }
//    
//    public TypeDef getTypeDef()
//    {
//        if (this.typeDef == null)
//        {
//            TypeService typeService = (TypeService) SingletonBeanLocator.getBean("typeService");
//            this.typeDef = typeService.getType(getOoType());
//        }
//        return this.typeDef;
//    }

    public String getOoType()
    {
        return getClass().getName();
    }
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }
}
