package org.otherobjects.cms.hibernate;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.otherobjects.cms.dao.SecureObject1Dao;
import org.otherobjects.cms.test.BaseDaoTestCase;

/**
 * Database needs to have the following:
 * 
 * 3 users 
 * 	- id:1 normal user
 * 	- id:2 admin user
 * 	- id:3 another normal user
 * 
 * 2 secure objects
 * 	- id:1, owned by user with id 1
 * 	- id:2, owned by user with id 3
 * @author joerg
 *
 */
public class SecureObject1DaoTest extends BaseDaoTestCase {
	
	private SecureObject1Dao secureObject1Dao;
	private AuthenticationManager authenticationManager;
	
	
	
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * this gets injected from spring because of AUTOWIRE_BY_NAME config
	 * @param secureObject1Dao
	 */
	public void setSecureObject1Dao(SecureObject1Dao secureObject1Dao) {
		this.secureObject1Dao = secureObject1Dao;
	}

	protected String[] getConfigLocations() {
        setAutowireMode(AUTOWIRE_BY_NAME);
        return new String[] {
                "file:src/test/resources/applicationContext-resources.xml",
                "file:src/test/resources/applicationContext-dao.xml",
                "file:src/test/resources/applicationContext-security.xml",
                "file:src/main/resources/applicationContext-dao.xml",
                "file:src/main/resources/applicationContext-security.xml",
            };
    }
	
	

	
	
	public void testAdminCanGet()
	{
		SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin")));
		secureObject1Dao.get(1L);
	}
	
	public void testCommonUserCantGet()
	{
		try{
			SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user", "user")));
			secureObject1Dao.getById(1L);
			
		}
		catch(Exception e)
		{
			fail();
		}
		
		
	}
	
	public void testCommonUserCanGetOwn()
	{
		try{
			SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user", "user")));
			secureObject1Dao.get(2L);
			fail();
		}
		catch(Exception e)
		{
			assertTrue(e instanceof AccessDeniedException);
		}
		
		
	}
	
}
