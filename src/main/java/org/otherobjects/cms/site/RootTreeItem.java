package org.otherobjects.cms.site;

public class RootTreeItem extends TreeItem
{
    private String path = "/";

    public RootTreeItem()
    {
        super(null);
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