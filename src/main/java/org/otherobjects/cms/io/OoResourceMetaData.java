package org.otherobjects.cms.io;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefImpl;

import flexjson.JSONSerializer;

/**
 * Simple bean to hold metaData for {@link OoResource}s. It's toString() method produces a JSON string
 * 
 * FIXME Finalise resource metaData content
 * 
 * @author joerg
 */
public class OoResourceMetaData
{
    private String title;
    private String description;
    private String keywords;
    private String author;

    // FIXME Impl properties support
    private HashMap<String, String> properties = new HashMap<String, String>();

    private TypeDefImpl typeDef;

    public OoResourceMetaData()
    {
    }

    /**
     * Creates object with given JSON data.
     * 
     * TODO This is a hardcoded impl. Should be using a JSON mapper.
     * 
     * @param json
     * @throws JSONException 
     */
    public OoResourceMetaData(String json) throws JSONException
    {
        JSONObject object = new JSONObject(json);
        setTitle(object.has("title") ? object.getString("title") : null);
        setDescription(object.has("description") ? object.getString("description") : null);
        setKeywords(object.has("keywords") ? object.getString("keywords") : null);
        setAuthor(object.has("author") ? object.getString("author") : null);

        if (object.has("typeDef"))
        {
            // FIXME Need to add full typeDef support
            JSONObject t = object.getJSONObject("typeDef");
            TypeDefImpl td = new TypeDefImpl();
            td.setName(t.getString("name"));
            td.setLabelProperty(t.getString("labelProperty"));
            JSONArray pArray = t.getJSONArray("properties");
            for (int i = 0; i < pArray.length(); i++)
            {
                JSONObject p = pArray.getJSONObject(i);
                String name = p.getString("name"); // required
                String type = p.getString("type"); // required
                String relatedType = p.has("relatedType") ? p.getString("relatedType") : null;
                String collectionElementType = p.has("collectionElementType") ? p.getString("collectionElementType") : null;
                boolean required = p.has("required") ? p.getBoolean("required") : false;
                boolean dynamic = p.has("dynamic") ? p.getBoolean("dynamic") : false;
                td.addProperty(new PropertyDefImpl(name, type, relatedType, collectionElementType, required, dynamic));
            }
            setTypeDef(td);
        }
    }

    public String toJSON()
    {
        return new JSONSerializer().exclude("class").serialize(this);
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public HashMap<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties)
    {
        this.properties = properties;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    public TypeDefImpl getTypeDef()
    {
        return typeDef;
    }

    public void setTypeDef(TypeDefImpl typeDef)
    {
        this.typeDef = typeDef;
    }
}
