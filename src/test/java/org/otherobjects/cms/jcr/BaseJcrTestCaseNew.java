package org.otherobjects.cms.jcr;

import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.otherobjects.cms.types.TypeDefBuilder;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.validation.BaseNodeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springmodules.jcr.jackrabbit.ocm.JcrMappingTemplate;

public abstract class BaseJcrTestCaseNew extends AbstractDependencyInjectionSpringContextTests
{
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected JcrMappingTemplate jcrMappingTemplate;
    protected TypeService typeService;
    protected UniversalJcrDao universalJcrDao;
    protected TypeDefBuilder typeDefBuilder;
    protected BaseNodeValidator baseNodeValidator;
    protected OtherObjectsConfigurator configurator;

    //bootstrap
    //    private DbSchemaInitialiser dbSchemaInitialiser;
    //    private JackrabbitInitialiser jackrabbitInitialiser;
    //    private OtherObjectsAdminUserCreator otherObjectsAdminUserCreator;
    //    private JackrabbitPopulater jackrabbitPopulater;
    //    private UserDao userDao;
    //
    //    private boolean dbSchemaInitialised = false;
    //    private boolean jackrabbitInitialised = false;
    //    private boolean adminCreated = false;
    //    private boolean jackrabbitPoppulated = false;
    //
    //    public void initialiseJackrabbit() throws Exception
    //    {
    //        if (!jackrabbitInitialised)
    //        {
    //            jackrabbitInitialiser.initialise();
    //            jackrabbitInitialised = true;
    //        }
    //    }
    //
    //    public void initialiseDbSchema() throws Exception
    //    {
    //        if (!dbSchemaInitialised)
    //        {
    //            dbSchemaInitialiser.initialise(true);
    //            dbSchemaInitialised = true;
    //        }
    //    }
    //
    //    public void populateJackrabbit() throws Exception
    //    {
    //        initialiseJackrabbit();
    //        createAdminUser();
    //        if (!jackrabbitPoppulated)
    //        {
    //            jackrabbitPopulater.populateRepository();
    //            jackrabbitPoppulated = true;
    //        }
    //    }
    //
    //    public void createAdminUser() throws Exception
    //    {
    //        if (!adminCreated)
    //        {
    //            initialiseDbSchema();
    //            otherObjectsAdminUserCreator.createAdminUser();
    //            adminCreated = true;
    //        }
    //    }
    //
    //    public void deleteRepository() throws Exception
    //    {
    //        RepositoryFactoryBean repositoryBean = (RepositoryFactoryBean) applicationContext.getBean("&repository");
    //        FileUtils.deleteDirectory(repositoryBean.getHomeDir().getFile());
    //
    //    }
    //
    //    public void fullBootstrap() throws Exception
    //    {
    //        populateJackrabbit();
    //    }
    //
    //    public void setDbSchemaInitialiser(DbSchemaInitialiser dbSchemaInitialiser)
    //    {
    //        this.dbSchemaInitialiser = dbSchemaInitialiser;
    //    }
    //
    //    public void setJackrabbitInitialiser(JackrabbitInitialiser jackrabbitInitialiser)
    //    {
    //        this.jackrabbitInitialiser = jackrabbitInitialiser;
    //    }
    //
    //    public void setOtherObjectsAdminUserCreator(OtherObjectsAdminUserCreator otherObjectsAdminUserCreator)
    //    {
    //        this.otherObjectsAdminUserCreator = otherObjectsAdminUserCreator;
    //    }
    //
    //    public void setJackrabbitPopulater(JackrabbitPopulater jackrabbitPopulater)
    //    {
    //        this.jackrabbitPopulater = jackrabbitPopulater;
    //    }

    @Override
    protected String[] getConfigLocations()
    {
        return new String[]{"org/otherobjects/cms/jcr/basejcrtest-context.xml"};
    }

    public void setBaseNodeValidator(BaseNodeValidator baseNodeValidator)
    {
        this.baseNodeValidator = baseNodeValidator;
    }

    public void setTypeDefBuilder(TypeDefBuilder typeDefBuilder)
    {
        this.typeDefBuilder = typeDefBuilder;
    }

    public void setJcrMappingTemplate(JcrMappingTemplate jcrMappingTemplate)
    {
        this.jcrMappingTemplate = jcrMappingTemplate;
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    public void setUniversalJcrDao(UniversalJcrDao universalJcrDao)
    {
        this.universalJcrDao = universalJcrDao;
    }

    public void setConfigurator(OtherObjectsConfigurator configurator)
    {
        this.configurator = configurator;
    }
}
