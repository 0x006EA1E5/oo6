package org.otherobjects.cms.jcr;

import javax.jcr.Session;
import javax.jcr.Workspace;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.MockAuthenticationManager;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationProvider;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationToken;
import org.apache.jackrabbit.core.WorkspaceImpl;
import org.otherobjects.cms.beans.BaseDynaNodeTest;

public class OtherObjectsJackrabbitSessionFactoryTest extends BaseDynaNodeTest {
	
	private OtherObjectsJackrabbitSessionFactory jcrSessionFactory;
	
	
	
	public void setJcrSessionFactory(
			OtherObjectsJackrabbitSessionFactory jcrSessionFactory) {
		this.jcrSessionFactory = jcrSessionFactory;
	}



	@Override
	protected void onSetUp() throws Exception {
		// TODO Auto-generated method stub
		super.onSetUp();
		Session session = jcrSessionFactory.getSession(null);
		boolean liveWorkspaceCreated = false;
		for(String wsp: session.getWorkspace().getAccessibleWorkspaceNames())
		{
			if(wsp.equals(OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME))
			{
				liveWorkspaceCreated = true;
				break;
			}
		}
		if(!liveWorkspaceCreated)
		{
			Workspace workspace = session.getWorkspace();
			((WorkspaceImpl) workspace).createWorkspace(OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME);
		}
	}
	
	public void testAdminGetsEditSession() throws Exception
	{
		// mock admin login
		new MockAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken("admin", "admin", new GrantedAuthority[]{new GrantedAuthorityImpl(OtherObjectsJackrabbitSessionFactory.EDITOR_ROLE_NAME)})
				);
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
