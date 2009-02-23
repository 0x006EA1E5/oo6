package org.otherobjects.cms.binding;

import org.otherobjects.cms.model.Role;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type
public class TestDbObject
{
    private Long id;
    
    private Role role;

    @Property(order=5)
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @Property(order=10, type=PropertyType.REFERENCE)
    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }
}
