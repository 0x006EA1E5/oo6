package org.otherobjects.cms.jcr;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.jackrabbit.ocm.spring.JackrabbitSessionFactory;


/**
 * This class overrides {@link JackrabbitSessionFactory} / {@link org.springmodules.jcr.JcrSessionFactory} respectively just to allow for 
 * creating sessions for different workspaces depending on the role(s) the current user has.
 * It also adds a getSession(String workspaceName) method to allow for explicitly obtaining sessions for specific workspace. 
 * @author joerg
 *
 */
public class OtherObjectsJackrabbitSessionFactory extends
		JackrabbitSessionFactory {
	
	public static final String LIVE_WORKSPACE_NAME = "live";
	public static final String EDIT_WORKSPACE_NAME = "default";
	public static final String EDITOR_ROLE_NAME = "ROLE_ADMIN";
	
	private String workspaceName;

	private Credentials credentials;
	
	@Override
	public Session getSession() throws RepositoryException {
		return getSession(isEditor() ? EDIT_WORKSPACE_NAME : LIVE_WORKSPACE_NAME);
	}
	
	public Session getSession(String workspaceName) throws RepositoryException {
		return addListeners(getRepository().login(credentials, workspaceName));
	}
	
	private boolean isEditor()
	{
		//FIXME if we don't have any authentication return true so that the default (existing) workspace is returned. Otherwise JackrabbitSessionFactory.registerNamespaces() will fail
		// when trying to obtain a session
		if(SecurityContextHolder.getContext().getAuthentication() == null)
			return true;
		
		for(GrantedAuthority ga: SecurityContextHolder.getContext().getAuthentication().getAuthorities())
		{
			if(ga.getAuthority().equals(EDITOR_ROLE_NAME))
				return true;
		}
		return false;
	}

	public String getWorkspaceName() {
		return workspaceName;
	}

	public void setWorkspaceName(String workspaceName) {
		super.setWorkspaceName(workspaceName);
		this.workspaceName = workspaceName;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		super.setWorkspaceName(workspaceName);
		this.credentials = credentials;
	}

	
}
