package org.otherobjects.cms.model;

import java.io.Serializable;

public class CompositeDatabaseId
{
    private String clazz;
    private Serializable id;

    public CompositeDatabaseId()
    {
    }

    public CompositeDatabaseId(String clazz, Serializable id)
    {
        this.clazz = clazz;
        this.id = id;
    }

    public String getClazz()
    {
        return clazz;
    }

    public void setClazz(String clazz)
    {
        this.clazz = clazz;
    }

    public Serializable getId()
    {
        return id;
    }

    public void setId(Serializable id)
    {
        this.id = id;
    }

    public String toString()
    {
        return clazz + "-" + id;
    }
}
