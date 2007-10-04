package org.otherobjects.cms.mail;


/**
 * Simple wrapper bean around an emails name and address which can then be used to create a {@link javax.mail.internet.InternetAddress}
 * 
 * @author joerg
 *
 */
public class EmailAddress {
	private String name;
	private String email;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public EmailAddress(String email)
	{
		this(null, email);
	}
	
	public EmailAddress(String name, String email)
	{
		this.name = name;
		this.email = email;
	}
	
	
	
}
