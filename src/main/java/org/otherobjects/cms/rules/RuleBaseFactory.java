//package org.otherobjects.cms.rules;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.Properties;
//
//import org.drools.RuleBase;
//import org.drools.StatelessSessionResult;
//import org.drools.agent.RuleAgent;
//import org.drools.base.ClassObjectFilter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.core.io.Resource;
//
//public class RuleBaseFactory implements InitializingBean, RuleExecutor
//{
//    protected final Logger logger = LoggerFactory.getLogger(getClass());
//
//    private Properties config;
//    private RuleAgent ruleAgent;
//    private Resource ruleDir;
//    private Resource cacheDir;
//
//    public void setRuleDir(Resource ruleDir)
//    {
//        this.ruleDir = ruleDir;
//    }
//
//    public void setCacheDir(Resource cacheDir)
//    {
//        this.cacheDir = cacheDir;
//    }
//
//    public void setConfig(Properties config)
//    {
//        this.config = config;
//    }
//
//    public void afterPropertiesSet() throws Exception
//    {
//        try
//        {
//            this.config.put("dir", ruleDir.getFile().getAbsolutePath());
//            this.config.put("localCacheDir", cacheDir.getFile().getAbsolutePath());
//            this.ruleAgent = RuleAgent.newRuleAgent(config);
//            //Assert.notNull(this.ruleAgent);
//        }
//        catch (Exception e)
//        {
//            logger.debug("Couldn't setup RuleAgent", e);
//            throw e;
//        }
//    }
//
//    public RuleBase getRuleBase()
//    {
//        return ruleAgent.getRuleBase();
//    }
//
//    /**
//     * Run the given facts through a stateless session configured with this RuleBaseFactory's rules and return an array of all objects from that session with the given class.
//     * @param facts - all the facts
//     * @param classOfResultToReturn - class of objects from session to return or null to return objects of class java.lang.Object
//     * @return Array of result objects matching classOfResultToReturn
//     */
//    @SuppressWarnings("unchecked")
//    public Object[] runInStatelessSession(Object[] facts, Class classOfResultToReturn)
//    {
//        if (classOfResultToReturn == null)
//            classOfResultToReturn = Object.class;
//
//        StatelessSessionResult results = getRuleBase().newStatelessSession().executeWithResults(facts);
//
//        Collection resultObjects = new ArrayList();
//        for (Iterator it = results.iterateObjects(new ClassObjectFilter(classOfResultToReturn)); it.hasNext();)
//        {
//            resultObjects.add(it.next());
//        }
//        return resultObjects.toArray();
//
//    }
//}
