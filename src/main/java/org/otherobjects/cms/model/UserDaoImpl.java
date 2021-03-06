package org.otherobjects.cms.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.otherobjects.cms.hibernate.GenericDaoHibernate;

/**
 * This class interacts with Spring's HibernateTemplate to save/delete and
 * retrieve User objects.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *   Modified by <a href="mailto:dan@getrolling.com">Dan Kibler</a>
 *   Extended to implement Acegi UserDetailsService interface by David Carter david@carter.net
 *   Modified by <a href="mailto:bwnoll@gmail.com">Bryan Noll</a> to work with 
 *   the new BaseDaoHibernate implementation that uses generics.
*/
public class UserDaoImpl extends GenericDaoHibernate<User, Serializable> implements UserDao, UserDetailsService
{

    public UserDaoImpl()
    {
        super(User.class);
    }

    /**
     * @see org.otherobjects.cms.model.UserDao#getUsers()
     */
    @SuppressWarnings("unchecked")
    public List<User> getUsers()
    {
        return getHibernateTemplate().find("from User u order by upper(u.username)");
    }

    /**
     * @see org.otherobjects.cms.model.UserDao#saveUser(org.appfuse.model.User)
     */
    public User saveUser(User user)
    {
        log.debug("user's id: " + user.getId());
        getHibernateTemplate().saveOrUpdate(user);
        // necessary to throw a DataIntegrityViolation and catch it in UserManager
        getHibernateTemplate().flush();
        return user;
    }

    /**
     * Overridden simply to call the saveUser method. This is happenening 
     * because saveUser flushes the session and saveObject of BaseDaoHibernate 
     * does not.
     */
    @Override
    public User save(User user)
    {
        return this.saveUser(user);
    }

    /** 
    * @see org.acegisecurity.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
    */
    @SuppressWarnings("unchecked")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        List<UserDetails> users = getHibernateTemplate().find("from User where username=?", username);
        if (users == null || users.isEmpty())
        {
            return null;
        }
        else
        {
            return users.get(0);
        }
    }
}
