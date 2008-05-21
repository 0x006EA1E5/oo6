package org.otherobjects.cms.jcr;

import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.types.TypeDefBuilder;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.validation.BaseNodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractTransactionalJUnit38SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springmodules.jcr.jackrabbit.ocm.JcrMappingTemplate;

@ContextConfiguration(locations = {"classpath:/org/otherobjects/cms/jcr/basejcrtest-context.xml"})
@TransactionConfiguration(transactionManager = "jcrTransactionManager", defaultRollback = true)
@Transactional
public abstract class BaseJcrTestCaseNew extends AbstractTransactionalJUnit38SpringContextTests
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
    protected BaseNodeValidator baseNodeValidator;
    
    @Autowired
    protected OtherObjectsConfigurator configurator;
    
    @Autowired
    protected DaoService daoService;
}
