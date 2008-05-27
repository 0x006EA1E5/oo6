package org.otherobjects.cms.model;

import org.springframework.security.userdetails.UserDetails;

public interface AccessControlled
{
    UserDetails getOwner();
}
