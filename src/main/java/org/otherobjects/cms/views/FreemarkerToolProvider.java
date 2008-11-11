package org.otherobjects.cms.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.otherobjects.cms.discovery.AnnotatedClassesScanner;
import org.otherobjects.cms.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

@SuppressWarnings("unchecked")
public class FreemarkerToolProvider implements InitializingBean
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AnnotatedClassesScanner scanner;
    private OtherObjectsConfigurator otherObjectsConfigurator;

    private Set<String> annotatedClasses;
    private String[] annotatedPackages;

    public void setScanner(AnnotatedClassesScanner scanner)
    {
        this.scanner = scanner;
    }

    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(scanner, "ClassPath scanner must be set");
        Assert.notNull(otherObjectsConfigurator, "OtherObjectsConfigurator must be set");
        List<String> packages = new ArrayList<String>();
        packages.add(otherObjectsConfigurator.getProperty("site.tool.packages"));
        packages.add(otherObjectsConfigurator.getProperty("otherobjects.tool.packages"));

        annotatedPackages = StringUtils.join(packages, ',').split(",");

        logger.info("Scanning the following packages: " + StringUtils.join(annotatedPackages, ','));

        annotatedClasses = scanner.findAnnotatedClasses(annotatedPackages, Tool.class);

        logger.info("Found the following tool entities: " + StringUtils.join(annotatedClasses, ','));
    }

    public Map<String, Object> getTools()
    {
        Map<String, Object> tools = new HashMap<String, Object>();
        for (String className : annotatedClasses)
        {
            try
            {
                Class toolClass = (Class<Tool>) Class.forName(className);
                Tool annotation = (Tool) toolClass.getAnnotation(Tool.class);
                tools.put(annotation.value(), toolClass.newInstance());
            }
            catch (Exception e)
            {
                logger.warn("Could not create tool className: " + className, e);
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

}
