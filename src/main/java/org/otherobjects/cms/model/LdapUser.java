package org.otherobjects.cms.model;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.ldap.support.DistinguishedName;

public class LdapUser implements UserDetails, Editable {
	
    private static final long serialVersionUID = -897291766936195523L;

    private String username;
	private String firstName;
	private String lastName;
	private String description;
	private String email;
	private DistinguishedName distinguishedName;
	
	public String getId() {
		return distinguishedName.toString();
	}

	public DistinguishedName getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(DistinguishedName distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public GrantedAuthority[] getAuthorities() {
		return new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_USER")};
	}

	public String getPassword() {
		return null;
	}

	public String getUsername() {
		return username;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return false;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

	public String getEditableId() {
		return getId();
	}

}
