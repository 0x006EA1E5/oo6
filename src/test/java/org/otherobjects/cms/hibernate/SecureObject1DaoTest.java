package org.otherobjects.cms.hibernate;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.otherobjects.cms.dao.SecureObject1Dao;
import org.otherobjects.cms.test.BaseDaoTestCase;

public class SecureObject1DaoTest extends BaseDaoTestCase {
	
	private SecureObject1Dao secureObject1Dao;
	
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
                "file:src/main/resources/applicationContext-dao.xml",
                "file:src/main/resources/applicationContext-security.xml"
            };
    }
	
	protected void setAdminUser()
	{
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pass", new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_ADMIN")}));
	}
	
	protected void setCommonUser()
	{
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "pass", new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_USER")}));
	}
	
	
	public void testAdminCanGet()
	{
		setAdminUser();
		secureObject1Dao.get(1L);
	}
	
	public void testCommonUserCantGet()
	{
		try{
			setCommonUser();
			secureObject1Dao.get(1L);
			fail();
		}
		catch(Exception e)
		{
			assertTrue(e instanceof AccessDeniedException);
		}
		
		
	}
	
}
