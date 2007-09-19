package org.otherobjects.cms.dao;

import java.util.List;

import javax.naming.Name;

import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.otherobjects.cms.model.LdapUser;
import org.springframework.ldap.ContextMapper;
import org.springframework.ldap.LdapTemplate;
import org.springframework.ldap.support.DirContextAdapter;
import org.springframework.ldap.support.DistinguishedName;


/**
 * Simple class implementing a User DAO for an LDAP backend. Implemented using Spring LDAP. See {@link http://static.springframework.org/spring-ldap/docs/1.1.2/reference/#d0e737}
 * 
 * Note: No precautions have been implemented yet to check the update method whether it needs to do a rename because of the uid having changed (that would be
 * neccessary as the uid is part of the DN which without renaming must not change)
 * 
 * Note: No handling of passwords yet
 * @author joerg
 *
 */
public class UserDAOLdap implements UserDetailsService {
	
	private LdapTemplate ldapTemplate;
	
	protected ContextMapper getContextMapper()
	{
		return new UserContextMapper();
	}
	
	protected void mapToContext(LdapUser user, DirContextAdapter context) {
	      context.setAttributeValues("objectclass", new String[] {"top","inetOrgPerson","organizationalPerson","person"});
	      context.setAttributeValue("cn", user.getFirstName() + " " + user.getLastName());
	      context.setAttributeValue("sn", user.getLastName());
	      context.setAttributeValue("description", user.getDescription());
	      context.setAttributeValue("mail", user.getEmail());
	      context.setAttributeValue("uid", user.getUsername());
	      context.setAttributeValue("givenname", user.getFirstName());
	   }

	   private static class UserContextMapper implements ContextMapper {
	      public Object mapFromContext(Object ctx) {
	         DirContextAdapter context = (DirContextAdapter)ctx;
	         LdapUser user = new LdapUser();
	         user.setDistinguishedName(new DistinguishedName(context.getDn()));
	         user.setFirstName(context.getStringAttribute("givenname"));
	         user.setLastName(context.getStringAttribute("sn"));
	         user.setEmail(context.getStringAttribute("mail"));
	         user.setUsername(context.getStringAttribute("uid"));
	         user.setDescription(context.getStringAttribute("description"));
	         return user;
	      }
	   }
	
	public List<LdapUser> listAll()
	{
		return (List<LdapUser>)ldapTemplate.search("ou=users", "(objectClass=person)", getContextMapper());
	}

	public UserDetails loadUserByUsername(String uid)
			throws UsernameNotFoundException {
		return (UserDetails) ldapTemplate.lookup(buildDN(uid, "users"), getContextMapper());
	}
	
	public LdapUser get(Name dn)
	{
		return (LdapUser) ldapTemplate.lookup(dn, getContextMapper());
	}

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}
	
	public void save(LdapUser ldapUser)
	{
		if(ldapUser.getDistinguishedName() != null) //update
			update(ldapUser);
		else // new user
			saveOnly(ldapUser);
	}
	
	private void saveOnly(LdapUser ldapUser) {
		ldapUser.setDistinguishedName((DistinguishedName) buildDN(ldapUser));
		DirContextAdapter context = new DirContextAdapter();
		mapToContext(ldapUser, context);
		ldapTemplate.bind(ldapUser.getDistinguishedName(), context, null);
	}

	private void update(LdapUser ldapUser) {
		Name dn = ldapUser.getDistinguishedName();
		DirContextAdapter context = (DirContextAdapter)ldapTemplate.lookup(dn);
		mapToContext(ldapUser, context);
		ldapTemplate.modifyAttributes(dn, context.getModificationItems());
	}

	public void delete(LdapUser ldapUser)
	{
		ldapTemplate.unbind(buildDN(ldapUser));
	}
	
	protected Name buildDN(LdapUser ldapUser)
	{
		return buildDN(ldapUser.getUsername(), "users");
	}

	protected Name buildDN(String username, String organizationalUnit) {
		DistinguishedName distinguishedName = new DistinguishedName();
		distinguishedName.add("ou", organizationalUnit);
		distinguishedName.add("uid", username);
		
		return distinguishedName;
	}

}
