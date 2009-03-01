package org.otherobjects.cms.model;


/**
 * Document those property names that can't be used. Better still name space them?
 * 
 * FIXME Our standard props should be namespaced eg ooLabel?
 * FIXME Note that BaseCompents must implement code field is used in colllections
 * 
 * @author rich
 */
public abstract class BaseComponent
{
    public BaseComponent()
    {
    }

    public String getOoType()
    {
        return getClass().getName();
    }
    
    public void setOoType(String ooType)
    {
        // ignore
    }

    public String getOoLabel()
    {
        return toString();
    }
    
    public void setOoLabel(String label)
    {
        // ignore
    }
}
