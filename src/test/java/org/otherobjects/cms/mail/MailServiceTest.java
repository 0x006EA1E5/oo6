package org.otherobjects.cms.mail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

@SuppressWarnings("unchecked")
@ContextConfiguration(locations = {"file:./src/test/java/org/otherobjects/cms/mail/mail-context.xml"})
public class MailServiceTest extends AbstractJUnit38SpringContextTests
{
    @Autowired
    private MailService mailService;

    public void testSimpleVelocityMail()
    {
        Wiser wiser = null;
        try
        {
            wiser = new Wiser();
            wiser.setPort(2500); // Default is 25
            wiser.start();

            FreemarkerMail mail = new FreemarkerMail();

            mail.setFromAddress(new EmailAddress("joe@woerd.com"));
            mail.addToRecipient(new EmailAddress("joe@woerd.com"));
            mail.setSubject("Test email");

            Map model = new HashMap();
            String test1Value = "This is the value of test1";
            model.put("test1", test1Value);

            mail.setModel(model);
            mail.setBodyTemplateResourcePath("DummyMailTemplate.ftl");

            mailService.send(mail);

            for (WiserMessage message : wiser.getMessages())
            {
                MimeMessage mess = message.getMimeMessage();
                assertEquals("simple: " + test1Value, mess.getContent());
                assertEquals("joe@woerd.com", mess.getAllRecipients()[0].toString());
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if (wiser != null)
                wiser.stop();
        }
    }

    public void testSimplePlusHtmlVelocityMail()
    {
        Wiser wiser = null;
        try
        {
            wiser = new Wiser();
            wiser.setPort(2500); // Default is 25
            wiser.start();

            FreemarkerMail mail = new FreemarkerMail();

            mail.setFromAddress(new EmailAddress("joe@woerd.com"));
            mail.addToRecipient(new EmailAddress("joe@woerd.com"));
            mail.setSubject("Test email");

            Map model = new HashMap();
            String test1Value = "This is the value of test1";
            model.put("test1", test1Value);

            mail.setModel(model);
            mail.setBodyTemplateResourcePath("DummyMailTemplate.ftl");
            mail.setHtmlBodyTemplateResourcePath("DummyHtmlMailTemplate.ftl");

            mailService.send(mail);

            for (WiserMessage message : wiser.getMessages())
            {
                MimeMessage mess = message.getMimeMessage();
                assertTrue(mess.getContent() instanceof Multipart);
                Multipart part = (Multipart) mess.getContent();
                assertTrue(part.getContentType().startsWith("multipart/alternative"));
                Part plain = part.getBodyPart(0);
                assertEquals("simple: " + test1Value, plain.getContent());

                Part html = part.getBodyPart(1);
                assertEquals("html: " + test1Value, html.getContent());
                assertEquals("joe@woerd.com", mess.getAllRecipients()[0].toString());
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if (wiser != null)
                wiser.stop();
        }
    }

    public void testSimpleMailWithWiser()
    {
        Wiser wiser = null;
        try
        {
            wiser = new Wiser();

            wiser.setPort(2500); // Default is 25
            wiser.start();

            FreemarkerMail mail = new FreemarkerMail();

            mail.setFromAddress(new EmailAddress("joe@woerd.com"));
            mail.addToRecipient(new EmailAddress("joe@woerd.com"));
            mail.setSubject("Test email");

            String bodyText = "This is a test email body";
            mail.setBody(bodyText);

            mailService.send(mail);

            for (WiserMessage message : wiser.getMessages())
            {
                MimeMessage mess = message.getMimeMessage();
                assertEquals(bodyText, mess.getContent());
                assertEquals("joe@woerd.com", mess.getAllRecipients()[0].toString());
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if (wiser != null)
                wiser.stop();
        }
    }

    public void testAttachment()
    {
        Wiser wiser = null;
        try
        {
            wiser = new Wiser();

            wiser.setPort(2500); // Default is 25
            wiser.start();

            FreemarkerMail mail = new FreemarkerMail();

            mail.setFromAddress(new EmailAddress("joe@woerd.com"));
            mail.addToRecipient(new EmailAddress("joe@woerd.com"));
            mail.setSubject("Test email");

            String bodyText = "This is a test email body";
            mail.setBody(bodyText);

            mail.addAttachment("classpath:/org/otherobjects/cms/mail/DummyPic.jpg");

            mailService.send(mail);

            for (WiserMessage message : wiser.getMessages())
            {
                MimeMessage mess = message.getMimeMessage();
                assertEquals("joe@woerd.com", mess.getAllRecipients()[0].toString());
                Multipart main = (Multipart) mess.getContent();
                assertTrue(main.getContentType().startsWith("multipart/mixed"));

                Part plain = main.getBodyPart(0);
                assertEquals(bodyText, plain.getContent());

                Part attachment = main.getBodyPart(1);

                assertTrue(attachment.getContentType().startsWith("image/jpeg"));
                assertEquals("DummyPic.jpg", attachment.getFileName());
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if (wiser != null)
                wiser.stop();
        }
    }

}
