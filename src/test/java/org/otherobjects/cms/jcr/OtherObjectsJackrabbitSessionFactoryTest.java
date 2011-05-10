package org.otherobjects.cms.jcr;

import java.util.Arrays;

import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.jackrabbit.core.WorkspaceImpl;
import org.otherobjects.cms.authentication.MockAuthenticationManager;
import org.otherobjects.cms.bootstrap.OtherObjectsAdminUserCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

public class OtherObjectsJackrabbitSessionFactoryTest extends BaseJcrTestCase
{
    @Autowired
    private OtherObjectsJackrabbitSessionFactory jcrSessionFactory;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        Session session = jcrSessionFactory.getSession(null);
        boolean liveWorkspaceCreated = false;
        for (String wsp : session.getWorkspace().getAccessibleWorkspaceNames())
        {
            if (wsp.equals(OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME))
            {
                liveWorkspaceCreated = true;
                break;
            }
        }
        if (!liveWorkspaceCreated)
        {
            Workspace workspace = session.getWorkspace();
            ((WorkspaceImpl) workspace).createWorkspace(OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME);
        }
    }

    public void testAdminGetsEditSession() throws Exception
    {
        // mock admin login
        new MockAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken("admin", "admin", Arrays.asList(new GrantedAuthority[]{new GrantedAuthorityImpl(
                OtherObjectsAdminUserCreator.DEFAULT_ADMIN_ROLE_NAME)})));
        System.out.println("workspace: " + jcrSessionFactory.getSession().getWorkspace().getName());
        assertTrue(jcrSessionFactory.getSession().getWorkspace().getName().equals(OtherObjectsJackrabbitSessionFactory.EDIT_WORKSPACE_NAME));
        SecurityContextHolder.clearContext();
    }

    public void testAnonymousUserGetsLiveSession() throws Exception
    {
        AnonymousAuthenticationProvider anonymousAuthenticationProvider = new AnonymousAuthenticationProvider();
        anonymousAuthenticationProvider.setKey("testkey");
        AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("testkey", "anonymous", Arrays.asList(new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_ANONYMOUS")}));
        SecurityContextHolder.getContext().setAuthentication(anonymousAuthenticationProvider.authenticate(anonymousAuthenticationToken));

        System.out.println("workspace: " + jcrSessionFactory.getSession().getWorkspace().getName());
        assertTrue(jcrSessionFactory.getSession().getWorkspace().getName().equals(OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME));
        SecurityContextHolder.clearContext();
    }

}
