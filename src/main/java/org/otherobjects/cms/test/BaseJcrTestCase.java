package org.otherobjects.cms.test;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.MockAuthenticationManager;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationProvider;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationToken;
import org.apache.jackrabbit.ocm.spring.JcrMappingTemplate;
import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.jcr.OtherObjectsJackrabbitSessionFactory;
import org.otherobjects.cms.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.AbstractTransactionalSpringContextTests;

/**
 * Base class for running Jcr tests. This class extends {@code AbstractTransactionalSpringContextTests} so
 * that dependencies are injected by Spring. Transactions are automatically created and rolled back
 * before each test.
 * 
 * @author rich
 */
public abstract class BaseJcrTestCase extends AbstractTransactionalSpringContextTests
{
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected ResourceBundle rb;
    protected JcrMappingTemplate jcrMappingTemplate;

    @Override
    protected String[] getConfigLocations()
    {
        setAutowireMode(AUTOWIRE_BY_TYPE);
        return new String[]{"file:src/test/resources/applicationContext-resources.xml", "file:src/test/resources/applicationContext-repository.xml"};
    }

    public BaseJcrTestCase()
    {
        // Since a ResourceBundle is not required for each class, just
        // do a simple check to see if one exists
        String className = this.getClass().getName();

        try
        {
            rb = ResourceBundle.getBundle(className);
        }
        catch (MissingResourceException mre)
        {
            //log.warn("No resource bundle found for: " + className);
        }
    }

    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        //cleanUpRepository();

        SingletonBeanLocator.setStaticBeanFactory(applicationContext.getBeanFactory());
        super.onSetUpBeforeTransaction();
    }

    public void setJcrMappingTemplate(JcrMappingTemplate jcrMappingTemplate)
    {
        this.jcrMappingTemplate = jcrMappingTemplate;
    }

	protected void adminLogin() {
		// pretend an editor session
		// fake admin
		User admin = new User("admin");
		admin.setId(new Long(1));
		
		SecurityContextHolder.getContext().setAuthentication(new MockAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(admin, "admin", new GrantedAuthority[]{new GrantedAuthorityImpl(OtherObjectsJackrabbitSessionFactory.EDITOR_ROLE_NAME)})
		));
	}

	protected void anoymousLogin() {
		// pretend anonymous user
	    AnonymousAuthenticationProvider anonymousAuthenticationProvider = new AnonymousAuthenticationProvider();
	    anonymousAuthenticationProvider.setKey("testkey");
	    AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("testkey", "anonymous", new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_ANONYMOUS")});
	    SecurityContextHolder.getContext().setAuthentication(anonymousAuthenticationProvider.authenticate(anonymousAuthenticationToken));
	}

	protected void logout() {
		SecurityContextHolder.clearContext();
	}

    //    public void exportDocument(String filePath, String nodePath, boolean skipBinary, boolean noRecurse)
    //    {
    //        try
    //        {
    //            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(filePath));
    //            ContentHandler handler = null;// new org.apache.xml.serialize.XMLSerializer(os, null).asContentHandler();
    //            session.exportDocumentView(nodePath, handler, skipBinary, noRecurse);
    //            os.flush();
    //            os.close();
    //        }
    //        catch (Exception e)
    //        {
    //            System.out.println("Impossible to export the content from : " + nodePath);
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    public void importDocument(String filePath, String nodePath)
    //    {
    //        try
    //        {
    //            BufferedInputStream is = new BufferedInputStream(new FileInputStream(filePath));
    //            session.importXML(nodePath, is, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
    //            session.save();
    //            is.close();
    //        }
    //        catch (Exception e)
    //        {
    //            System.out.println("Impossible to import the content from : " + nodePath);
    //            e.printStackTrace();
    //        }
    //
    //    }

}
