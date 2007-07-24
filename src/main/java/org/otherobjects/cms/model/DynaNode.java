package org.otherobjects.cms.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.jackrabbit.ocm.manager.collectionconverter.impl.ManagedHashMap;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.workbench.WorkbenchItem;
import org.springframework.util.Assert;

/**
 * A dynamic object represents a data node in the content repository.
 * 
 * <p>Dynamic nodes can change their properties and validators at runtime
 * providing a very flexible data model.
 * 
 * <p>TODO Add support for description, icon and image generators
 * <br>TODO Equals, hashCode, serialableId builders
 */
@SuppressWarnings("unchecked")
public class DynaNode implements CmsNode, WorkbenchItem
{
    /** GUID */
    private String id;

    /** Property to be used an the label (human friendly identifier) for this node. */
    private String labelProperty;

    /** The path of this node's parent. Must end with a forward slash. */
    private String path;

    /** System readable identifier eg filename. Used an the name of the node. Must not contain a slash. */
    private String code;

    /* Additional textual information about this item */
    /* Icon representing this node */
    /* Image representing this node */

    /** The defining type for this node */
    private String ooType;

    private Map<String, Object> data = new ManagedHashMap();

    //FIXME This is temporary until we figure out what to do with CGLIB and JCR OCM.
    private static TypeService typeService;

    public DynaNode(TypeDef typeDef)
    {
        setOoType(typeDef.getName());
    }

    public DynaNode()
    {
    }

    /**
     * Creates a node of the specified type. The type definition 
     * in required to ensure that this node conforms.
     * 
     * @param type the type name
     */
    public DynaNode(String type)
    {
        setOoType(type);
    }

    public void setOoType(String ooType)
    {
        if (typeService == null)
            typeService = (TypeService) SingletonBeanLocator.getBean("typeService");
        TypeDef typeDef = typeService.getType(ooType);

        setLabelProperty(typeDef.getLabelProperty());
        this.ooType = ooType;
    }

    /**
     * Return the path of this node for use with JCR. The jcr path is comprised of
     * the path and the code.
     * 
     * @return the jcr path
     */
    public String getJcrPath()
    {
        if (path == null || code == null)
            return null;

        Assert.isTrue(path.endsWith("/"), "Path must end with a forward slash");
        return getPath() + getCode();
    }

    /**
     * Sets the JCR path for this node. The path and code will be inferred form this.
     * 
     * @param jcrPath
     */
    public void setJcrPath(String jcrPath)
    {
        //        if (jcrPath == null)
        //        {
        //            setCode(null);
        //            setPath(null);
        //            return;
        //        }
        Assert.notNull(jcrPath, "jcrPath must not be null");

        Assert.isTrue(jcrPath.lastIndexOf("/") >= 0, "jcrPath must contain at least one forward slash");
        Assert.isTrue(!jcrPath.endsWith("/"), "jcrPath must not end with a forward slash");

        int slashPos = jcrPath.lastIndexOf("/");
        setPath(jcrPath.substring(0, slashPos + 1));
        setCode(jcrPath.substring(slashPos + 1));
    }

    public Object get(String name)
    {
        try
        {
            return PropertyUtils.getProperty(this, name);
        }
        catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

        //        if (name != null && name.indexOf('.') > 0)
        //            return getProperty(name);
        //        else
        //            return getData().get(name);
    }

    public Object getProperty(String name)
    {
        try
        {
            return PropertyUtils.getNestedProperty(getData(), name);
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not fetch property value.", e);
        }
    }

    public void set(String name, String value)
    {
        try
        {
            PropertyUtils.setNestedProperty(getData(), name, value);
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not fetch property value.", e);
        }
    }

    @Override
    public String toString()
    {
        return "[" + getOoType() + "] " + getLabel();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        Assert.notNull(path, "path may not be null.");
        // FIXME       Assert.doesNotContain(path, ".", "path may not contain a period.");
        if (!path.endsWith("/"))
            path += "/";
        this.path = path;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public void setData(Map<String, Object> data)
    {
        this.data = data;
    }

    public String getLabel()
    {
        return (String) (get(labelProperty) != null ? get(labelProperty) : getCode());
    }

    public void setLabel(String label)
    {
        //TODO What is labelProperty is missing
        set(labelProperty, label);
    }

    public void set(String key, Object value)
    {
        getData().put(key, value);
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        Assert.notNull(code, "code may not be null.");
        Assert.doesNotContain(code, "/", "code may not contain a slash.");
        this.code = code;
    }

    public String getOoType()
    {
        return ooType;
    }

    /**
     * FIXME Better name needed?
     */
    public String getLinkPath()
    {
        String linkPath = getJcrPath().replaceAll("/site/", "/go/");
        if (!linkPath.contains(".") && !linkPath.endsWith("/"))
            linkPath += "/";
        return linkPath;
    }

    public String getLabelProperty()
    {
        return labelProperty;
    }

    public void setLabelProperty(String labelProperty)
    {
        this.labelProperty = labelProperty;
    }
}
