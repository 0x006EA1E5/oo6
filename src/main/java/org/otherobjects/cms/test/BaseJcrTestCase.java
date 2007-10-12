package org.otherobjects.cms.test;

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
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.OtherObjectsJackrabbitSessionFactory;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
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
    protected DaoService daoService;
    protected TypeService typeService;
    protected UniversalJcrDao universalJcrDao;

    @Override
    protected String[] getConfigLocations()
    {
        setAutowireMode(AUTOWIRE_BY_TYPE);
        return new String[]{"file:src/test/resources/applicationContext-resources.xml", "file:src/test/resources/applicationContext-repository.xml"};

        //        setAutowireMode(AUTOWIRE_BY_TYPE);
        //        //setAutowireMode(AUTOWIRE_BY_NAME);
        //        return new String[]{"file:src/test/resources/applicationContext-resources.xml", "file:src/test/resources/applicationContext-repository.xml"};//,"file:src/main/resources/otherobjects.resources/config/applicationContext-dao.xml"};
    }

    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        SingletonBeanLocator.setStaticBeanFactory(applicationContext.getBeanFactory());
        super.onSetUpBeforeTransaction();
    }

    public void setJcrMappingTemplate(JcrMappingTemplate jcrMappingTemplate)
    {
        this.jcrMappingTemplate = jcrMappingTemplate;
    }

    protected void adminLogin()
    {
        // pretend an editor session
        // fake admin
        User admin = new User("admin");
        admin.setId(new Long(1));

        SecurityContextHolder.getContext().setAuthentication(
                new MockAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(admin, "admin", new GrantedAuthority[]{new GrantedAuthorityImpl(
                        OtherObjectsJackrabbitSessionFactory.EDITOR_ROLE_NAME)})));
    }

    protected void anoymousLogin()
    {
        // pretend anonymous user
        AnonymousAuthenticationProvider anonymousAuthenticationProvider = new AnonymousAuthenticationProvider();
        anonymousAuthenticationProvider.setKey("testkey");
        AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("testkey", "anonymous", new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_ANONYMOUS")});
        SecurityContextHolder.getContext().setAuthentication(anonymousAuthenticationProvider.authenticate(anonymousAuthenticationToken));
    }

    protected void logout()
    {
        SecurityContextHolder.clearContext();
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    protected void registerType(Class<?> cls) throws Exception
    {
        AnnotationBasedTypeDefBuilder b = new AnnotationBasedTypeDefBuilder();
        TypeDef typeDef = b.getTypeDef(cls);
        typeService.registerType(typeDef);
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    public void setUniversalJcrDao(UniversalJcrDao universalJcrDao)
    {
        this.universalJcrDao = universalJcrDao;
    }
}
