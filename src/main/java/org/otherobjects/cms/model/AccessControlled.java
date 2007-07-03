package org.otherobjects.cms.model;

import org.acegisecurity.userdetails.UserDetails;

public interface AccessControlled {
	public UserDetails getOwner();
}
