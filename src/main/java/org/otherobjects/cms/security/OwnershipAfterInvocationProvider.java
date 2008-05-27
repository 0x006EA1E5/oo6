package org.otherobjects.cms.security;

import java.util.Arrays;
import java.util.Iterator;

import org.otherobjects.cms.model.AccessControlled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.afterinvocation.AfterInvocationProvider;

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
@SuppressWarnings("unchecked")
public class OwnershipAfterInvocationProvider implements AfterInvocationProvider, MessageSourceAware
{

    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    
    private final String processConfigAttribute = "AFTER_OWNERSHIP";

    private MessageSourceAccessor messages;
    private Class<?> processDomainObjectClass = AccessControlled.class;

    public void setMessageSource(MessageSource messageSource)
    {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config, Object returnedObject) throws AccessDeniedException
    {
        Iterator iter = config.getConfigAttributes().iterator();

        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute) iter.next();

            if (this.supports(attr))
            {
                // Need to make an access decision on this invocation

                if (!getProcessDomainObjectClass().isAssignableFrom(returnedObject.getClass()))
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Return object is not applicable for this provider, skipping");
                    }

                    return returnedObject;
                }

                for (Object element : Arrays.asList(authentication.getAuthorities()))
                {
                    GrantedAuthority grantedAuthority = (GrantedAuthority) element;
                    if (ADMIN_ROLE.equals(grantedAuthority.getAuthority()))
                    {
                        return returnedObject;
                    }
                }

                // ok, we know already we are dealing with an AccessControlled object
                AccessControlled accessControlled = (AccessControlled) returnedObject;
                if (accessControlled.getOwner().equals(authentication.getPrincipal()))
                {
                    return returnedObject;
                }
                else
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Denying access");
                    }

                    throw new AccessDeniedException(messages.getMessage("OwnershipAfterInvocationProvider.notOwner", new Object[]{authentication.getName(), returnedObject},
                            "Authentication {0} is not owner of the domain object {1}"));
                }
            }
        }

        return returnedObject;
    }

    public boolean supports(ConfigAttribute attribute)
    {
        return ((attribute.getAttribute() != null) && attribute.getAttribute().equals(this.processConfigAttribute));
    }

    /**
     * this provider supports all classes implementing the {@link AccessControlled} interface
     * 
     */

    public boolean supports(Class clazz)
    {
        return true;
    }

    public Class getProcessDomainObjectClass()
    {
        return processDomainObjectClass;
    }

    public void setProcessDomainObjectClass(Class processDomainObjectClass)
    {
        this.processDomainObjectClass = processDomainObjectClass;
    }

}
