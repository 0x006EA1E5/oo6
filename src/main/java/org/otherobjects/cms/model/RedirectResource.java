package org.otherobjects.cms.model;

public class RedirectResource extends DynaNode
{
    private String label;
    private String url;
    private Boolean temporary;

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
