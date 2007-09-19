package org.otherobjects.cms.dao;

import java.util.List;

import org.acegisecurity.userdetails.UserDetails;
import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.model.LdapUser;
import org.springframework.ldap.EntryNotFoundException;
import org.springframework.test.AbstractTransactionalSpringContextTests;

public class UsearDaoLdapTest extends AbstractTransactionalSpringContextTests {
	
	private UserDAOLdap userDaoLdap;
	
	
	public void setUserDaoLdap(UserDAOLdap userDaoLdap) {
		this.userDaoLdap = userDaoLdap;
	}


	@Override
	protected String[] getConfigLocations() {
		setAutowireMode(AUTOWIRE_BY_TYPE);
        return new String[]{"file:src/test/resources/applicationContext-resources.xml", "file:src/main/resources/otherobjects.resources/config/applicationContext-dao.xml"};
	}
	
	
	@Override
    protected void onSetUpInTransaction() throws Exception
    {
        //cleanUpRepository();

        SingletonBeanLocator.setStaticBeanFactory(applicationContext.getBeanFactory());
        super.onSetUpBeforeTransaction();
    }
	
	public void testGetByUsername()
	{
		UserDetails user = userDaoLdap.loadUserByUsername("user1");
		assertNotNull(user);
		
		System.out.println("email: " + ((LdapUser)user).getEmail());
		
	}
	
	public void testlistAll()
	{
		List<LdapUser> all = userDaoLdap.listAll();
		assertTrue(all.size() > 0);
		
		System.out.println(all.get(0).getDistinguishedName());
	}
	
	public void testSaveModifyDelete()
	{
		LdapUser user = new LdapUser();
		user.setUsername("user3");
		user.setFirstName("User3");
		user.setLastName("User3");
		user.setDescription("User 3 description");
		user.setEmail("user3@otherobjects.org");
		
		userDaoLdap.save(user);
		
		user = userDaoLdap.get(user.getDistinguishedName());
		assertNotNull(user);
		
		String changedDescription = "A changed Description";
		
		user.setDescription(changedDescription);
		
		userDaoLdap.save(user);
		
		user = userDaoLdap.get(user.getDistinguishedName());
		
		assertEquals(changedDescription, user.getDescription());
		
		userDaoLdap.delete(user);
		try{
			userDaoLdap.get(user.getDistinguishedName());
			fail();
		}
		catch(EntryNotFoundException e)
		{
			
		}
	}
}
