package org.otherobjects.cms.mail;

import org.springframework.test.AbstractSingleSpringContextTests;

public class MailServiceTest extends AbstractSingleSpringContextTests {

	@Override
	protected String[] getConfigLocations() {
		return new String[]{"file:src/test/resources/applicationContext-mail.xml"};
	}

	public void testSimpleMail()
	{
		MailService mailService = (MailService) getApplicationContext().getBean("mailService");
		VelocityMail mail = new VelocityMail();
		
		mail.setFromAddress(new EmailAddress("joe@woerd.com"));
		mail.addToRecipient(new EmailAddress("joe@woerd.com"));
		mail.setSubject("Test email");
		mail.setBody("This is a test email body");
		
		mailService.send(mail);
		
	}
}
