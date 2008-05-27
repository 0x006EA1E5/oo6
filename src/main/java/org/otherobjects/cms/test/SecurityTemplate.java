package org.otherobjects.cms.test;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.bootstrap.OtherObjectsAdminUserCreator;
import org.otherobjects.cms.dao.UserDao;
import org.otherobjects.cms.model.User;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationProvider;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;

/**
 * 
 * @author joerg
 *
 */
public class SecurityTemplate
{
    //TODO I guess we need a executeAsUser(Long id, SecurityCallback action) method

    public Object executeAsAdmin(SecurityCallback action)
    {
        // try to get the real admin user
        UserDao userDao = null;
        try
        {
            userDao = (UserDao) SingletonBeanLocator.getBean("userDao");
        }
        catch (Exception e)
        {
            // TODO Explain why we ignore exception
        }

        //TODO if we are in a real context, don't we need to setup a dummy user with ROLE_ADMIN to then be able to load the actual admin user iwth the DAO?

        Authentication auth = null;
        User adminUser = null;

        if (userDao != null)
        {
            adminUser = (User) userDao.loadUserByUsername(OtherObjectsAdminUserCreator.DEFAULT_ADMIN_USER_NAME);
        }

        if (adminUser != null)
        {
            auth = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
        }

        if (auth == null) // no real admin user, or no userDao or no context, fake an admin
        {
            auth = new UsernamePasswordAuthenticationToken(OtherObjectsAdminUserCreator.DEFAULT_ADMIN_USER_NAME, null, new GrantedAuthority[]{
                    new GrantedAuthorityImpl(OtherObjectsAdminUserCreator.DEFAULT_ADMIN_ROLE_NAME), new GrantedAuthorityImpl(OtherObjectsAdminUserCreator.DEFAULT_USER_ROLE_NAME)});
        }

        return execute(action, auth);
    }

    public Object executeAsUser(User user, SecurityCallback action)
    {
        return execute(action, new UsernamePasswordAuthenticationToken(user, null, new GrantedAuthority[]{new GrantedAuthorityImpl(OtherObjectsAdminUserCreator.DEFAULT_USER_ROLE_NAME)}));

    }

    public Object executeAsDummyUser(SecurityCallback action)
    {
        User dummyUser = new User();
        dummyUser.setEnabled(true);
        dummyUser.setAccountExpired(false);
        dummyUser.setAccountLocked(false);
        dummyUser.setCredentialsExpired(false);
        dummyUser.setEmail("dummy@otherobjects.org");
        dummyUser.setUsername("securityTemplate-dummyuser");
        dummyUser.setFirstName("dummy");
        dummyUser.setLastName("user");
        dummyUser.setId(Long.MAX_VALUE); //FIXME this could potentially clash with a real user in the application

        return executeAsUser(dummyUser, action);
    }

    public Object executeAsAnonymous(SecurityCallback action)
    {
        AnonymousAuthenticationProvider anonymousAuthenticationProvider = new AnonymousAuthenticationProvider();
        anonymousAuthenticationProvider.setKey("testkey");
        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken("testkey", "anonymous", new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_ANONYMOUS")});
        return execute(action, auth);
    }

    public Object execute(SecurityCallback action, Authentication auth)
    {
        // keep a record of the current authentication, if any
        Authentication previousAuth = SecurityContextHolder.getContext().getAuthentication();
        Object result;
        try
        {
            SecurityContextHolder.getContext().setAuthentication(auth);
            result = action.doWithSecurityContext();
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("SecurityCallback threw exception!");
        }
        finally
        {
            SecurityContextHolder.getContext().setAuthentication(previousAuth);
        }

        return result;
    }
}
