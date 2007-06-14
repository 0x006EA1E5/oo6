package org.otherobjects.cms.model;

import java.util.HashMap;
import java.util.Map;

import org.otherobjects.cms.types.TypeDef;

public class CmsNode
{
    /** GUID */
    private String id;

    /** The path of this node */
    private String path;

    /* System readable identifier eg filename */
    private String code;

    /* Human readable identifier */
    private String label;

    /* Additional textual information about this item */
    private String description;

    /** The defining type for this node */
    private TypeDef typeDef;

    private Map<String, Object> data = new HashMap<String, Object>();

    public CmsNode()
    {
    }

    /**
     * Creates a node of the specified type. The type definition 
     * in required to ensure that this node conforms.
     * 
     * @param type the type name
     */
    public CmsNode(String type)
    {
        //setType(type);
    }

    @Override
    public String toString()
    {
        return "x";// + getLabel() + " [" + getTypeDef().getName() + "]";
    }

    public Object get(String name)
    {
        return getData().get(name);
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
        if (this.label == null)
            return (String) getData().get("title");
        else
            return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public TypeDef getTypeDef()
    {
        return typeDef;
    }

    public void setTypeDef(TypeDef typeDef)
    {
        this.typeDef = typeDef;
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

    public String getTitle()
    {
        return (String) getData().get("title");
    }

    public String getContent()
    {
        return (String) getData().get("content");
    }

    public void setTitle(String title)
    {
        getData().put("title", title);
    }

    public void setContent(String content)
    {
        getData().put("content", content);
    }

}
