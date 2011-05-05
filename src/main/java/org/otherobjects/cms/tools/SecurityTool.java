package org.otherobjects.cms.tools;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.security.SecurityUtil;
import org.otherobjects.cms.views.Tool;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.taglibs.velocity.Authz;
import org.springframework.security.taglibs.velocity.AuthzImpl;
import org.springframework.stereotype.Component;

/**
 * Based on Spring Security Taglib.
 * 
 * @author rich
 */
@Component
@Tool
public class SecurityTool
{
    /**
     * Determines if the user has or doesn't have certain roles.
     *           
     * @param ifAllGranted A comma separated list of roles which the user must all possess
     * @param ifAnyGranted A comma separated list of roles, one of which the user must possess
     * @param ifNotGranted A comma separated list of roles which the user must not have
     */
    public boolean authorize(String ifAllGranted, String ifAnyGranted, String ifNotGranted)
    {
        Authz a = new AuthzImpl();
        boolean all = StringUtils.isNotEmpty(ifAllGranted) ? a.allGranted(ifAllGranted) : true;
        boolean any = StringUtils.isNotEmpty(ifAnyGranted) ? a.anyGranted(ifAnyGranted) : true;
        boolean none = StringUtils.isNotEmpty(ifNotGranted) ? a.noneGranted(ifNotGranted) : true;
        return all && any && none;
    }

    
    /**
     * Determines if the user has or doesn't have certain roles.
     *           
     * @param ifAnyGranted A comma separated list of roles, one of which the user must possess
     */
    public boolean authorize(List <String> ifAnyGranted)
    {
        StringBuilder buf=new StringBuilder();
        for (String auth:ifAnyGranted)
        {
            buf.append(auth+",");
        }
        //delete last comma
        buf.deleteCharAt(buf.length()-1);
        Authz a = new AuthzImpl();
        boolean any = StringUtils.isNotEmpty(buf.toString()) ? a.anyGranted(buf.toString()) : true;
        return any;
    }
    
    /**
     * Returns the current user.
     * 
     * @return
     */
    public UserDetails getUser()
    {
        return SecurityUtil.getCurrentUser();
    }

}
