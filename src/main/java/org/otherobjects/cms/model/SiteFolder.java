package org.otherobjects.cms.model;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.Url;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;
import org.otherobjects.cms.util.StringUtils;
import org.otherobjects.framework.SingletonBeanLocator;

import flexjson.JSON;

@SuppressWarnings("serial")
@Type(labelProperty = "label")
public class SiteFolder extends Folder
{
    private String label;
    private String cssClass;
    private String defaultView;
    private String defaultPage;
    private List<TemplateMapping> templateMappings;

    private List<String> allowedTypes;
    private Url url;
    private boolean inMenu = true; // FIXME Merge this with publishing options
    private String tags;
    private PublishingOptions publishingOptions;
    private List<String> requiredRoles,requiredEditorRoles;
    private String notAuthorizedResource;
    //FIXME, temporal list to emulate maps, shall we create a pseudo map class?
    //private List<MapEntry> properties;

    @Property(order = 5)
    public String getCode()
    {
        return super.getCode();
    }

    public void setCode(String code)
    {
        super.setCode(code);
    }

    public String getTags()
    {
        return tags;
    }

    public void setTags(String tags)
    {
        this.tags = tags;
    }

    @Override
    public boolean isFolder()
    {
        return true;
    }

    public List<TypeDef> getAllAllowedTypes()
    {
        TypeService typeService = ((TypeService) SingletonBeanLocator.getBean("typeService"));
        if (getAllowedTypes() != null && getAllowedTypes().size() > 0)
        {
            List<TypeDef> types = new ArrayList<TypeDef>();
            for (String t : getAllowedTypes())
            {
                types.add(typeService.getType(t));
            }
            return types;
        }
        else
        {
            return new ArrayList<TypeDef>();//(List<TypeDef>) typeService.getTypesBySuperClass(BaseNode.class);
        }
    }

    /**
     * Returns the view to use when listing objects. Uses the defaultView property if set, otherwise returns "list".
     * 
     * @return the view to use
     */
    public String getView()
    {
        return getDefaultView() != null ? getDefaultView() : "list";
    }

    @Property(order = 40)
    public String getCssClass()
    {
        return cssClass;
    }

    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }

    @JSON(include = false)
    @Property(order = 50, collectionElementType = PropertyType.STRING)
    public List<String> getAllowedTypes()
    {
        return allowedTypes;
    }

    public void setAllowedTypes(List<String> allowedTypes)
    {
        this.allowedTypes = allowedTypes;
    }

    @Override
    @Property(order = 20)
    public String getLabel()
    {
        return this.label;
    }

    @Override
    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(order = 25)
    public String getDefaultPage()
    {
        return defaultPage;
    }

    public void setDefaultPage(String defaultPage)
    {
        this.defaultPage = defaultPage;
    }

    public int getDepth()
    {
        return getHref().getDepth();
    }

    public Url getHref()
    {
        if (url == null)
            url = new Url(getOoUrlPath());
        return url;
    }

    @Property(order = 35, type = PropertyType.BOOLEAN)
    public boolean isInMenu()
    {
        return inMenu;
    }

    public void setInMenu(boolean inMenu)
    {
        this.inMenu = inMenu;
    }

    @Property(order = 500, type = PropertyType.COMPONENT)
    public PublishingOptions getPublishingOptions()
    {
        return publishingOptions;
    }

    public void setPublishingOptions(PublishingOptions publishingOptions)
    {
        this.publishingOptions = publishingOptions;
    }

    @JSON(include = false)
    @Property(order = 60, collectionElementType = PropertyType.STRING, help = "Specify a list of roles a user needs to be a member off to access this folder. Any of those roles will do.")
    public List<String> getRequiredRoles()
    {
        return requiredRoles;
    }

    public void setRequiredRoles(List<String> requiredRoles)
    {
        this.requiredRoles = requiredRoles;
    }

    @JSON(include = false)
    @Property(order = 70)
    public String getNotAuthorizedResource()
    {
        return notAuthorizedResource;
    }

    public void setNotAuthorizedResource(String notAuthorizedResource)
    {
        this.notAuthorizedResource = notAuthorizedResource;
    }

    public String getNavigationLabel()
    {
        if (getPublishingOptions() != null)
            return (StringUtils.isNotBlank(getPublishingOptions().getNavigationLabel())) ? getPublishingOptions().getNavigationLabel() : getLabel();
        else
            return getLabel();
    }

    @Property(order = 45, help = "Supported views are: list,thumbnails")
    public String getDefaultView()
    {
        return defaultView;
    }

    public void setDefaultView(String defaultView)
    {
        this.defaultView = defaultView;
    }

    public void setTemplateMappings(List<TemplateMapping> templateMappings)
    {
        this.templateMappings = templateMappings;
    }

    @Property(order = 10, collectionElementType = PropertyType.COMPONENT)
    public List<TemplateMapping> getTemplateMappings()
    {
        return templateMappings;
    }

    //TODO maybe create the actual TM in method
    public void addTemplateMapping(TemplateMapping tm)
    {
        if (templateMappings == null)
            templateMappings = new ArrayList<TemplateMapping>();
        this.templateMappings.add(tm);
    }

    public void removeTemplateMapping(String typeName)
    {
        if (templateMappings != null)
            this.templateMappings.remove(typeName);
    }

    public TemplateMapping getTemplateMapping(String typeName)
    {
        //return (TemplateMapping) MapUtils.getObject(templateMappings, typeName);
        if (templateMappings == null)
            return null;
        for (TemplateMapping tm : templateMappings)
        {
            if (tm.getType().getName().equalsIgnoreCase(typeName))
                return tm;
        }
        return null;
    }

    public SiteFolder getParentFolder(UniversalJcrDao universalJcrDao)
    {
        if (getPath().equals("/"))
            return null;
        String path = getPath().substring(0, getPath().lastIndexOf("/"));
        return (SiteFolder) universalJcrDao.getByPath(path);
    }

    public BaseNode getParentNode(UniversalJcrDao universalJcrDao)
    {
        return getParentFolder(universalJcrDao);
    }

    public void setRequiredEditorRoles(List<String> requiredEditorRoles) {
        this.requiredEditorRoles = requiredEditorRoles;
    }
   
    @JSON(include = false)
    @Property(order = 65, collectionElementType = PropertyType.STRING, help = "Specify a list of roles a user needs to be a member off to access this folder in workbench. Any of those roles will do.")
    public List<String> getRequiredEditorRoles() {
        return requiredEditorRoles;
    }

//  public void setProperties(List<MapEntry> properties) {
//      this.properties = properties;
//  }
//    @Property(order = 100,collectionElementType = PropertyType.COMPONENT,relatedType="org.otherobjects.cms.model.MapEntry")
//  public List<MapEntry> getProperties() {
//      return properties;
//  }
//
//    //TODO add check for duplicate key
//  public void putMapProperty(String key, String value) {
//      if(null==properties)
//          properties=new ArrayList<MapEntry>();
//      properties.add(new MapEntry(key,value));
//  }
//
//  public BaseNode getMapProperty(String key) {
//      if(null==properties)
//          return null;
//      for(MapEntry e:properties)
//      {
//          if (e.getKey().equals(key))
//              return e.getValue();
//      }
//      return null;
//  }
//
//  public void removeMapProperty(String key) {
//      for(MapEntry e:properties)
//      {
//          if (e.getKey().equals(key)){
//              properties.remove(e);
//          }
//      }
//  }
    
}
