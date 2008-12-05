package org.otherobjects.cms.views;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.otherobjects.cms.discovery.AnnotatedClassesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SuppressWarnings("unchecked")
public class FreemarkerToolProvider implements InitializingBean, ApplicationContextAware
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AnnotatedClassesScanner scanner;
    private OtherObjectsConfigurator otherObjectsConfigurator;
    private ApplicationContext applicationContext;

    private Set<String> annotatedClasses;
    private String[] annotatedPackages;


    public void setScanner(AnnotatedClassesScanner scanner)
    {
        this.scanner = scanner;
    }

    public void afterPropertiesSet() throws Exception
    {
//        Assert.notNull(scanner, "ClassPath scanner must be set");
//        Assert.notNull(otherObjectsConfigurator, "OtherObjectsConfigurator must be set");
//        List<String> packages = new ArrayList<String>();
//        packages.add(otherObjectsConfigurator.getProperty("site.tool.packages"));
//        packages.add(otherObjectsConfigurator.getProperty("otherobjects.tool.packages"));
//
//        annotatedPackages = StringUtils.join(packages, ',').split(",");
//
//        logger.info("Scanning the following packages: " + StringUtils.join(annotatedPackages, ','));
//
//        annotatedClasses = scanner.findAnnotatedClasses(annotatedPackages, Tool.class);
//
//        logger.info("Found the following tool entities: " + StringUtils.join(annotatedClasses, ','));
        getTools();
    }

    public Map<String, Object> getTools()
    {
//        Map<String, Object> tools = new HashMap<String, Object>();
//        for (String className : annotatedClasses)
//        {
//            try
//            {
//                Class toolClass = (Class<Tool>) Class.forName(className);
//                Tool annotation = (Tool) toolClass.getAnnotation(Tool.class);
//                tools.put(annotation.value(), toolClass.newInstance());
//            }
//            catch (Exception e)
//            {
//                logger.warn("Could not create tool className: " + className, e);
//            }
//        }
//        return tools;
        Map<String, Object> tools = new HashMap<String, Object>();
        for (String beanName : applicationContext.getBeanDefinitionNames())
        {
            try
            {
                Object tool = applicationContext.getBean(beanName);
                Tool annotation = (Tool) tool.getClass().getAnnotation(Tool.class);
                if(annotation!=null)
                    tools.put(annotation.value(), tool);
            }
            catch (Exception e)
            {
                logger.warn("Error scanning bean for @Tool: " + beanName, e);
            }
        }
        return tools;
    }

    public String[] getAnnotatedPackages()
    {
        return annotatedPackages;
    }

    public void setOtherObjectsConfigurator(OtherObjectsConfigurator otherObjectsConfigurator)
    {
        this.otherObjectsConfigurator = otherObjectsConfigurator;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
