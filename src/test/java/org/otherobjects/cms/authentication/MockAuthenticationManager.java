package org.otherobjects.cms.authentication;

import org.springframework.security.authentication.AbstractAuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
19   * Simply accepts as valid whatever is passed to it, if <code>grantAccess</code> is set to <code>true</code>.
20   *
21   * @author Ben Alex
22   * @author Wesley Hall
23   * @version $Id: MockAuthenticationManager.java 2217 2007-10-27 00:45:30Z luke_t $
24   */
public class MockAuthenticationManager extends AbstractAuthenticationManager {
      //~ Instance fields ================================================================================================
  
      private boolean grantAccess = true;
  
      //~ Constructors ===================================================================================================
  
      public MockAuthenticationManager(boolean grantAccess) {
          this.grantAccess = grantAccess;
      }
 
      public MockAuthenticationManager() {
          super();
      }
  
      //~ Methods ========================================================================================================
  
      public Authentication doAuthentication(Authentication authentication)
          throws AuthenticationException {
          if (grantAccess) {
              return authentication;
          } else {
              throw new BadCredentialsException("MockAuthenticationManager instructed to deny access");
          }
      }
  }
