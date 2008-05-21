package org.otherobjects.cms.test;

import junit.framework.TestCase;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.bootstrap.OtherObjectsAdminUserCreator;
import org.otherobjects.cms.model.User;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationTrustResolverImpl;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.util.AuthorityUtils;

public class SecurityTemplateTest extends TestCase
{
    public void testAnonymousExecute()
    {
        SecurityTemplate st = new SecurityTemplate();

        st.executeAsAnonymous(new SecurityCallback()
        {

            public Object doWithSecurityContext() throws OtherObjectsException
            {
                assertTrue(new AuthenticationTrustResolverImpl().isAnonymous(SecurityContextHolder.getContext().getAuthentication()));
                assertTrue(AuthorityUtils.userHasAuthority("ROLE_ANONYMOUS"));
                return null;
            }
        });
    }

    public void testDummyUserExecute()
    {
        SecurityTemplate st = new SecurityTemplate();

        st.executeAsDummyUser(new SecurityCallback()
        {

            public Object doWithSecurityContext() throws OtherObjectsException
            {
                assertTrue(AuthorityUtils.userHasAuthority(OtherObjectsAdminUserCreator.DEFAULT_USER_ROLE_NAME));
                assertFalse(AuthorityUtils.userHasAuthority(OtherObjectsAdminUserCreator.DEFAULT_ADMIN_ROLE_NAME));
                return null;
            }
        });
    }

    public void testAdminUserExecute()
    {
        SecurityTemplate st = new SecurityTemplate();

        String username = "securityTemplate-dummyuser";
        User dummyUser = new User();
        dummyUser.setEnabled(true);
        dummyUser.setAccountExpired(false);
        dummyUser.setAccountLocked(false);
        dummyUser.setCredentialsExpired(false);
        dummyUser.setEmail("dummy@otherobjects.org");
        dummyUser.setUsername(username);
        dummyUser.setFirstName("dummy");
        dummyUser.setLastName("user");
        dummyUser.setId(Long.MAX_VALUE);

        Authentication auth = new UsernamePasswordAuthenticationToken(dummyUser, null, new GrantedAuthority[]{new GrantedAuthorityImpl(OtherObjectsAdminUserCreator.DEFAULT_USER_ROLE_NAME)});
        SecurityContextHolder.getContext().setAuthentication(auth);

        st.executeAsAdmin(new SecurityCallback()
        {

            public Object doWithSecurityContext() throws OtherObjectsException
            {
                assertTrue(AuthorityUtils.userHasAuthority(OtherObjectsAdminUserCreator.DEFAULT_ADMIN_ROLE_NAME));

                return null;
            }
        });

        assertTrue(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername().equals(username));
    }
}
