package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.model.BaseNode;

public class TreeItem
{

    private BaseNode item;
    private List<TreeItem> children = new ArrayList<TreeItem>();

    public TreeItem(BaseNode item)
    {
        this.item = item;
    }

    public BaseNode getItem()
    {
        return item;
    }

    public void setItem(BaseNode item)
    {
        this.item = item;
    }

    public List<TreeItem> getChildren()
    {
        return children;
    }

    public void setChildren(List<TreeItem> children)
    {
        this.children = children;
    }

    public String getPath()
    {
        String jcrPath = getItem().getJcrPath();
        if (!jcrPath.endsWith("/"))
            jcrPath = jcrPath + "/";
        return jcrPath;
    }

    @Override
    public String toString()
    {
        return getPath();
    }

}