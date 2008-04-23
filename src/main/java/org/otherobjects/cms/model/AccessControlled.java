package org.otherobjects.cms.model;

import org.springframework.security.userdetails.UserDetails;

public interface AccessControlled {
	public UserDetails getOwner();
}
