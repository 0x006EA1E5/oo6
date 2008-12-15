package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.hibernate.GenericDaoHibernate;

/**
 * This class interacts with Spring's HibernateTemplate to save/delete and
 * retrieve Role objects.
 *
 * @author <a href="mailto:bwnoll@gmail.com">Bryan Noll</a> 
 */
public class RoleDaoImpl extends GenericDaoHibernate<Role, Long> implements RoleDao
{

    public RoleDaoImpl()
    {
        super(Role.class);
    }

    @SuppressWarnings("unchecked")
    public Role getRoleByName(String rolename)
    {
        List roles = getHibernateTemplate().find("from Role where name=?", rolename);
        if (roles.isEmpty())
        {
            return null;
        }
        else
        {
            return (Role) roles.get(0);
        }
    }

    public void removeRole(String rolename)
    {
        Object role = getRoleByName(rolename);
        getHibernateTemplate().delete(role);
    }
}
