package org.otherobjects.cms.mail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.otherobjects.cms.OtherObjectsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class MailService implements ResourceLoaderAware {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private ResourceLoader resourceLoader;
	
	private JavaMailSender mailSender;
	private VelocityEngine velocityEngine;
	
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}
	
	private Resource getResource(String resourcePath)
	{
		return resourceLoader.getResource(resourcePath);
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
		
	}
	
	public void send(final VelocityMail mail)
	{
		if(!mail.isValid())
			throw new OtherObjectsException("VelocityMail is not valid");

		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				mimeMessage.setSubject(mail.getSubject());
				mimeMessage.setFrom(translateAddress(mail.getFromAddress()));
				
				for(EmailAddress toAddr : mail.getToRecipients())
				{
					mimeMessage.addRecipient(Message.RecipientType.TO, translateAddress(toAddr));
				}
				
				for(EmailAddress ccAddr : mail.getCcRecipients())
				{
					mimeMessage.addRecipient(Message.RecipientType.CC, translateAddress(ccAddr));
				}
				
				for(EmailAddress bccAddr : mail.getBccRecipients())
				{
					mimeMessage.addRecipient(Message.RecipientType.BCC, translateAddress(bccAddr));
				}
				
				if(mail.getReplyToAddress() != null)
				{
					InternetAddress[] replyTo = {translateAddress(mail.getReplyToAddress())};
					mimeMessage.setReplyTo(replyTo);
				}
				
				mimeMessage.setSentDate(new Date());
				
				boolean hasPlainTextBody = mail.hasBody();
				boolean hasHtmlBody = mail.hasHtmlBody();
				boolean hasTextualContent = hasPlainTextBody || hasHtmlBody;
				boolean hasBothTextualVersions = hasPlainTextBody && hasHtmlBody;
				
				if(!mail.hasAttachments()) // no attachments
				{
					if(hasBothTextualVersions) // both types of textual content
		            {
		                Multipart multiPart = new MimeMultipart("alternative");
		                multiPart.addBodyPart(getPlainTextBodyPart());
		                multiPart.addBodyPart(getHtmlBodyPart());
		                mimeMessage.setContent(multiPart);
		            }
		            else if (hasPlainTextBody)
		            {
		            	mimeMessage.setContent(getPlainTextBody(), "text/plain");
		            }
		            else if(hasHtmlBody)
		            {
		            	mimeMessage.setContent(getHtmlBody(), "text/html");
		            	mimeMessage.setDisposition("inline");
		            }
				}
				else // with attachments
				{
					Multipart mixedMultipart = new MimeMultipart("mixed");
		            
		            // if there is textual content use it and put it in first
		            if(hasTextualContent)
		            {
		                //textual content first
		                if(hasBothTextualVersions)
		                {
		                    Multipart multiPart = new MimeMultipart("alternative");
		                    multiPart.addBodyPart(getPlainTextBodyPart());
		                    multiPart.addBodyPart(getHtmlBodyPart());
		                    BodyPart textBodyPart = new MimeBodyPart();
		                    textBodyPart.setContent(multiPart);
		                    mixedMultipart.addBodyPart(textBodyPart);
		                }
		                else if (hasPlainTextBody)
		                {
		                    mixedMultipart.addBodyPart(getPlainTextBodyPart());
		                }
		                else if(hasHtmlBody)
		                {
		                    mixedMultipart.addBodyPart(getHtmlBodyPart());
		                }
		            }
		            
		            //now attachments
		            for(String attResourcePath : mail.getAttachments())
		            {
		            	mixedMultipart.addBodyPart(getAttachmentBodyPart(attResourcePath));
		            }
		            mimeMessage.setContent(mixedMultipart);
				}
			}

			private BodyPart getAttachmentBodyPart(String attResourcePath) throws MessagingException, IOException {
				Resource attachmentResource = getResource(attResourcePath);
				
				BodyPart attachmentBodyPart = new MimeBodyPart();
	            attachmentBodyPart.setDataHandler(new DataHandler(new URLDataSource(attachmentResource.getURL())));
	            attachmentBodyPart.setFileName(attachmentResource.getFilename());
				return attachmentBodyPart;
			}

			private String getHtmlBody() {
				if(StringUtils.isNotBlank(mail.getHtmlBody()))
					return mail.getHtmlBody();
				else if(mail.getModel() != null && StringUtils.isNotBlank(mail.getHtmlBodyTemplateResourcePath()))
				{
					return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, mail.getHtmlBodyTemplateResourcePath(), mail.getModel());
				}
				else
					return null;
			}

			private String getPlainTextBody() {
				if(StringUtils.isNotBlank(mail.getBody()))
					return mail.getBody();
				else if(mail.getModel() != null && StringUtils.isNotBlank(mail.getBodyTemplateResourcePath()))
				{
					return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, mail.getBodyTemplateResourcePath(), mail.getModel());
				}
				else
					return null;
			}

			private BodyPart getHtmlBodyPart() throws MessagingException {
				BodyPart messagebody = new MimeBodyPart();
		        messagebody.setContent(getHtmlBody(), "text/html");
		        messagebody.setDisposition("inline");
		        return messagebody;
			}

			private BodyPart getPlainTextBodyPart() throws MessagingException {
				BodyPart messagebody = new MimeBodyPart();
		        messagebody.setText(getPlainTextBody());
		        return messagebody;
			}
		};
		try{
			mailSender.send(preparator);
			logger.info("mail to " + mail.getToRecipients().get(0).getEmail() + " from " + mail.getFromAddress().getEmail() + " with subject: " + mail.getSubject() + " was sent successfully!");
		}
		catch(MailException e)
		{
			logger.warn("mail to " + mail.getToRecipients().get(0).getEmail() + " from " + mail.getFromAddress().getEmail() + " with subject: " + mail.getSubject() + " couldn't be send!", e);
		}
	}
	
	private InternetAddress translateAddress(EmailAddress emailAddress) throws UnsupportedEncodingException, AddressException
	{
		if(StringUtils.isBlank(emailAddress.getName()))
			return new InternetAddress(emailAddress.getEmail(), emailAddress.getName());
		else
			return new InternetAddress(emailAddress.getEmail());
	}
	
}
