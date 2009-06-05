package org.otherobjects.cms.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Entity
@Type(label = "Localized Message", description = "Storse user editable messages for the site.", labelProperty = "code", store = "hibernate")
public class LocalizedMessage implements Serializable, Editable
{
    private static final long serialVersionUID = 1653349608216248497L;

    protected String id;
    protected String locale = "en";
    protected String message;

    public LocalizedMessage()
    {
    }

    public LocalizedMessage(String id, String locale, String message)
    {
        this.id = id;
        this.locale = locale;
        this.message = message;
    }

    @Transient
    public String getOoLabel()
    {
        return getId();
    }

    @Transient
    public String getEditableId()
    {
        return getClass().getName() + "-" + getId();
    }

    @Id
    @Property(type = PropertyType.STRING, required = true, order = 0)
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Column(nullable = false, length = 5)
    @Property(type = PropertyType.STRING, required = true, order = 10)
    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    @Column(nullable = false, length = 255)
    @Property(type = PropertyType.STRING, required = true, order = 20)
    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
