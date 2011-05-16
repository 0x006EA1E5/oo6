package org.otherobjects.cms.jcr;

import java.util.Arrays;

import org.apache.jackrabbit.ocm.spring.JcrMappingTemplate;
import org.otherobjects.cms.authentication.MockAuthenticationManager;
import org.otherobjects.cms.bootstrap.OtherObjectsAdminUserCreator;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeDefBuilder;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = {"file:src/test/java/org/otherobjects/cms/jcr/jcr-test-context.xml"})
@TransactionConfiguration(transactionManager = "jcrTransactionManager", defaultRollback = true)
@Transactional
public abstract class BaseJcrTestCase extends AbstractTransactionalJUnit4SpringContextTests
{
    @Autowired
    protected JcrMappingTemplate jcrMappingTemplate;

    @Autowired
    protected TypeService typeService;

    @Autowired
    protected UniversalJcrDao universalJcrDao;

    @Autowired
    protected TypeDefBuilder typeDefBuilder;

    @Autowired
    protected OtherObjectsConfigurator configurator;

    @Autowired
    protected DaoService daoService;

    protected void registerType(Class<?> cls) throws Exception
    {
        AnnotationBasedTypeDefBuilder b = new AnnotationBasedTypeDefBuilder();
        TypeDef typeDef = b.getTypeDef(cls);
        typeService.registerType(typeDef);
    }

    protected void registerType(TypeDef typeDef)
    {
        typeService.registerType(typeDef);
    }

    protected void adminLogin()
    {
        // pretend to be an editor
        // fake admin
        User admin = new User("admin");
        admin.setId(new Long(1));

        SecurityContextHolder.getContext().setAuthentication(
                new MockAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(admin, "admin", Arrays.asList(new GrantedAuthority[]{new GrantedAuthorityImpl(OtherObjectsAdminUserCreator.DEFAULT_ADMIN_ROLE_NAME)}))));
    }

    protected void anoymousLogin()
    {
        // pretend to be an anonymous user
        AnonymousAuthenticationProvider anonymousAuthenticationProvider = new AnonymousAuthenticationProvider();
        anonymousAuthenticationProvider.setKey("testkey");
        AnonymousAuthenticationToken anonymousAuthenticationToken 
            = new AnonymousAuthenticationToken("testkey", "anonymous", 
                    Arrays.asList(new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_ANONYMOUS")}));
        SecurityContextHolder.getContext().setAuthentication(anonymousAuthenticationProvider.authenticate(anonymousAuthenticationToken));
    }

    protected void logout()
    {
        SecurityContextHolder.clearContext();
    }

}
