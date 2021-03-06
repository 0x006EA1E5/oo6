package org.otherobjects.cms.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;
import org.springframework.security.core.GrantedAuthority;

/**
 * This class is used to represent available roles in the database.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *         Version by Dan Kibler dan@getrolling.com
 *         Extended to implement Acegi GrantedAuthority interface
 *         by David Carter david@carter.net
 */
@Entity
@Table(name = "role")
@Type(label = "Role", description = "", labelProperty = "name", store = "hibernate")
public class Role implements Serializable, GrantedAuthority, Editable
{
    private static final long serialVersionUID = 3690197650654049848L;
    private Long id;
    private String name;
    private String description;
    private TypeDef typeDef;

    public Role()
    {
    }

    public Role(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    @Transient
    public String getEditableId()
    {
        return getClass().getName() + "-" + getId();
    }

    @Transient
    public String getOoIcon()
    {
        return "/otherobjects/static/icons/key.png";
    }

    @Transient
    public TypeDef getTypeDef()
    {
        return this.typeDef;
    }

    public void setTypeDef(TypeDef typeDef)
    {
        this.typeDef = typeDef;
    }

    @Transient
    public String getOoLabel()
    {
        return getName();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Long getId()
    {
        return this.id;
    }

    /**
     * @see org.acegisecurity.GrantedAuthority#getAuthority()
     */
    @Transient
    public String getAuthority()
    {
        return getName();
    }

    @Column(length = 20, unique = true)
    @Property(type = PropertyType.STRING, required = true, label = "Code", size = 30, 
                help = "Must be uppercase and start ROLE_", valang = "{ name : ? IS NOT NULL AND match('ROLE_[A-Z0-9_]*',?) == TRUE : 'Must start ROLE_' : 'validation.role.name.pattern' }")
    public String getName()
    {
        return this.name;
    }

    @Column(length = 64)
    @Property(type = PropertyType.TEXT, required = false, label = "Description", size = 250)
    public String getDescription()
    {
        return this.description;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof Role))
            return false;

        final Role role = (Role) o;

        return !(this.name != null ? !this.name.equals(role.name) : role.name != null);

    }

    @Override
    public int hashCode()
    {
        return (this.name != null ? this.name.hashCode() : 0);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(this.name).toString();
    }

    public int compareTo(Object o)
    {
        return 0;
    }
}
