package org.otherobjects.cms.model;

import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.jackrabbit.ocm.manager.collectionconverter.impl.ManagedHashMap;
import org.otherobjects.cms.OtherObjectsException;
import org.springframework.util.Assert;

/**
 * A CmsNode object represents a data node in the content repository.
 * 
 * <p>A node can be uniquely identified in 2 ways: by a GUID or by the jcrPath
 * (which is the concatenation of path and code).
 * 
 * <p>TODO Add support for description, icon and image generators
 * <p>TODO Equals, hashCode, serialableId builders
 */
@SuppressWarnings("unchecked")
public class CmsNode
{
    /** Property to be used an the label for this node. */
    private static final String LABEL_KEY = "title";

    /** GUID */
    private String id;

    /** The path of this node's parent. Must end with a forward slash. */
    private String path;

    /** System readable identifier eg filename */
    private String code;

    /* Human readable identifier */
    /* Additional textual information about this item */
    /* Icon representing this node */
    /* Image representing this node */

    /** The defining type for this node */
    private String ooType;

    private Map<String, Object> data = new ManagedHashMap();

    public CmsNode()
    {
        setOoType(getClass().getName());
    }

    /**
     * Creates a node of the specified type. The type definition 
     * in required to ensure that this node conforms.
     * 
     * @param type the type name
     */
    public CmsNode(String type)
    {
        setOoType(type);
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

        Assert.isTrue(path.endsWith("/"), "Path must end with a forward slash" );
        return getPath() + getCode();
    }

    /**
     * Sets the JCR path for this node. The path and code will be inferred form this.
     * 
     * @param jcrPath
     */
    public void setJcrPath(String jcrPath)
    {
        if (jcrPath == null)
        {
            setCode(null);
            setPath(null);
            return;
        }

        Assert.isTrue(jcrPath.lastIndexOf("/") >= 0, "jcrPath must contain at least one forward slash");
        Assert.isTrue(!jcrPath.endsWith("/"), "jcrPath must not end with a forward slash");

        int slashPos = jcrPath.lastIndexOf("/");
        setPath(jcrPath.substring(0, slashPos + 1));
        setCode(jcrPath.substring(slashPos + 1));
    }

    public Object get(String name)
    {
        if (name != null && name.indexOf('.') > 0)
            return getProperty(name);
        else
            return getData().get(name);
    }

    public Object getProperty(String name)
    {
        try
        {
            // Property values are stored in the data map...
            return PropertyUtils.getNestedProperty(getData(), name.replaceAll("\\.","\\.data\\."));
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not fetch property value.", e);
        }
    }

    public void set(String name, String value)
    {
        getData().put(name, value);
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
        return (String) get(LABEL_KEY);
    }

    public void setLabel(String label)
    {
        set(LABEL_KEY, label);
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
        this.code = code;
    }

    public String getOoType()
    {
        return ooType;
    }

    public void setOoType(String ooType)
    {
        this.ooType = ooType;
    }

}
