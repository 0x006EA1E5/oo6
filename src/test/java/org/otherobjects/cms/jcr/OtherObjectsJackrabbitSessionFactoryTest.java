package org.otherobjects.cms.jcr;

import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.jackrabbit.core.WorkspaceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.MockAuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationProvider;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;

public class OtherObjectsJackrabbitSessionFactoryTest extends BaseJcrTestCaseNew
{
    @Autowired
    private OtherObjectsJackrabbitSessionFactory jcrSessionFactory;

    @Override
    protected void setUp() throws Exception
    {
        // TODO Auto-generated method stub
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
        new MockAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken("admin", "admin", new GrantedAuthority[]{new GrantedAuthorityImpl(
                OtherObjectsJackrabbitSessionFactory.EDITOR_ROLE_NAME)}));
        System.out.println("workspace: " + jcrSessionFactory.getSession().getWorkspace().getName());
        assertTrue(jcrSessionFactory.getSession().getWorkspace().getName().equals(OtherObjectsJackrabbitSessionFactory.EDIT_WORKSPACE_NAME));
        SecurityContextHolder.clearContext();
    }

    public void testAnonymousUserGetsLiveSession() throws Exception
    {
        AnonymousAuthenticationProvider anonymousAuthenticationProvider = new AnonymousAuthenticationProvider();
        anonymousAuthenticationProvider.setKey("testkey");
        AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("testkey", "anonymous", new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_ANONYMOUS")});
        SecurityContextHolder.getContext().setAuthentication(anonymousAuthenticationProvider.authenticate(anonymousAuthenticationToken));

        System.out.println("workspace: " + jcrSessionFactory.getSession().getWorkspace().getName());
        assertTrue(jcrSessionFactory.getSession().getWorkspace().getName().equals(OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME));
        SecurityContextHolder.clearContext();
    }

}
