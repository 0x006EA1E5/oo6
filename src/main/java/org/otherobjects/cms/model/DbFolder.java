package org.otherobjects.cms.model;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

import flexjson.JSON;

@SuppressWarnings("unchecked")
@Type(labelProperty="label")
public class DbFolder extends Folder
{
    private String label;
    private String mainType;
    private String mainTypeQuery;
    private String cssClass;

    
    @Property(order=0)
    public String getCode()
    {
        return super.getCode();
    }

    public void setCode(String code)
    {
        super.setCode(code);
    }

    public List getAllAllowedTypes()
    {
        return getAllowedTypes();
    }
    
    /**
     * Returns the view to use when listing objects. Currently we only support lists for DbFolders.
     * 
     * @return the view to use
     */
    public String getView()
    {
        return "list";
    }

    @JSON(include = false)
    //@Property(order = 50, collectionElementType = PropertyType.STRING)
    public List<String> getAllowedTypes()
    {
        // Only allowed type is the main type...    
        List types = new ArrayList();
        types.add(((TypeService) SingletonBeanLocator.getBean("typeService")).getType(this.mainType));
        return types;
    }

    public void setAllowedTypes(List allowedTypes)
    {
        // Not relevant for Database backed folders
    }

    @Override
    @Property(order = 10)
    public String getLabel()
    {
        return this.label;
    }

    @Override
    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(order = 20)
    public String getMainType()
    {
        return this.mainType;
    }

    public void setMainType(String mainType)
    {
        this.mainType = mainType;
    }

    @Property(order = 30)
    public String getMainTypeQuery()
    {
        return this.mainTypeQuery;
    }

    public void setMainTypeQuery(String mainTypeQuery)
    {
        this.mainTypeQuery = mainTypeQuery;
    }

    @Property(order = 40)
    public String getCssClass()
    {
        return this.cssClass;
    }

    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }

}
