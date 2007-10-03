package org.otherobjects.cms.model;

public class RedirectResource extends DynaNode
{
    private static final String ICON_PATH = "otherobjects.resources/static/icons/page-white-go.png";
    
    
    private String label;
    private String url;
    private Boolean temporary;

    @Override
    public String getOoIcon()
    {
        return ICON_PATH;
    }
     
    
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Boolean getTemporary()
    {
        return temporary;
    }

    public void setTemporary(Boolean temporary)
    {
        this.temporary = temporary;
    }

}
