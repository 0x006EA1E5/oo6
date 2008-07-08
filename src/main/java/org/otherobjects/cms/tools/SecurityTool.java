package org.otherobjects.cms.tools;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.taglibs.velocity.Authz;
import org.springframework.security.taglibs.velocity.AuthzImpl;
import org.springframework.security.userdetails.UserDetails;

/**
 * Based on Spring Security Taglib.
 * 
 * @author rich
 */
public class SecurityTool
{
    /**
     * A simple tag to output or not the body of the tag if the principal
     * has or doesn't have certain authorities.
     *           
     * @param ifNotGranted A comma separated list of roles which the user must not have for the body to be output
     * @param ifAllGranted A comma separated list of roles which the user must all possess for the body to be output.
     * @param ifAnyGranted A comma separated list of roles, one of which the user must possess for the body to be output.
     */
    public boolean authorize(String ifAllGranted, String ifAnyGranted, String ifNotGranted)
    {
        Authz a = new AuthzImpl();
        boolean all = StringUtils.isNotEmpty(ifAllGranted) ? a.allGranted(ifAllGranted) : true;
        boolean any = StringUtils.isNotEmpty(ifAnyGranted) ? a.anyGranted(ifAnyGranted) : true;
        boolean none = StringUtils.isNotEmpty(ifNotGranted) ? a.noneGranted(ifNotGranted) : true;
        return all && any && none;
    }

    public UserDetails getUser()
    {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
