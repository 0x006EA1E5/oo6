package org.otherobjects.cms.security;

import java.util.Arrays;
import java.util.Iterator;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.Authentication;
import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.afterinvocation.AfterInvocationProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.otherobjects.cms.model.AccessControlled;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This AfterInvocationProvider can be used on all domain objects that implement the {@link AccessControlled} interface and 
 * decides negatively if the current user is not the owner of the domain object. It is activated by the CofigAttribute 
 * AFTER_OWNERSHIP
 * 
 * Ownership is not checked if the current user has the ROLE_ADMIN in its authorities
 * 
 * @author joerg@woerd.org
 *
 */
public class OwnershipAfterInvocationProvider implements AfterInvocationProvider, MessageSourceAware {
	
	public final static String ADMIN_ROLE = "ROLE_ADMIN";
	public final String processConfigAttribute = "AFTER_OWNERSHIP";
	
	private MessageSourceAccessor messages;
	private Class processDomainObjectClass = AccessControlled.class;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);;
	}
	
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	
	
	public Object decide(Authentication authentication, Object object,
			ConfigAttributeDefinition config, Object returnedObject)
			throws AccessDeniedException {
		Iterator iter = config.getConfigAttributes();

        while (iter.hasNext()) {
            ConfigAttribute attr = (ConfigAttribute) iter.next();

            if (this.supports(attr)) {
                // Need to make an access decision on this invocation

                if (!getProcessDomainObjectClass().isAssignableFrom(returnedObject.getClass())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Return object is not applicable for this provider, skipping");
                    }

                    return returnedObject;
                }
                
                for(Iterator it = Arrays.asList(authentication.getAuthorities()).iterator(); it.hasNext();)
                {
                	GrantedAuthority grantedAuthority = (GrantedAuthority) it.next();
                	if(ADMIN_ROLE.equals(grantedAuthority.getAuthority()))
                	{
                		return returnedObject;
                	}
                }
                
                // ok, we know already we are dealing with an AccessControlled object
                AccessControlled accessControlled = (AccessControlled) returnedObject;
                if (accessControlled.getOwner().equals(authentication.getPrincipal())) {
                    return returnedObject;
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Denying access");
                    }

                    throw new AccessDeniedException(messages.getMessage(
                            "OwnershipAfterInvocationProvider.notOwner",
                            new Object[] {authentication.getName(), returnedObject},
                            "Authentication {0} is not owner of the domain object {1}"));
                }
            }
        }

        return returnedObject;
	}

	public boolean supports(ConfigAttribute attribute) {
		if ((attribute.getAttribute() != null) && attribute.getAttribute().equals(this.processConfigAttribute)) {
            return true;
        } else {
            return false;
        }
	}
	
	/**
	 * this provider supports all classes implementing the {@link AccessControlled} interface
	 * 
	 */
	public boolean supports(Class clazz) {
		return true;
	}

	public Class getProcessDomainObjectClass() {
		return processDomainObjectClass;
	}

	public void setProcessDomainObjectClass(Class processDomainObjectClass) {
		this.processDomainObjectClass = processDomainObjectClass;
	}

}
