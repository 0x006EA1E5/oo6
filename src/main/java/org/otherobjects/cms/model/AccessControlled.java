package org.otherobjects.cms.model;

import org.springframework.security.core.userdetails.UserDetails;


public interface AccessControlled
{
    UserDetails getOwner();
}
