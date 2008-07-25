//package org.otherobjects.cms.io;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.otherobjects.cms.test.BaseSharedContextTestCase;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;
//import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
//
//import freemarker.template.Configuration;
//import freemarker.template.Template;
//
////@ContextConfiguration(locations = {"file:./src/test/java/org/otherobjects/cms/io/freemarker-context.xml"})
//public class OoResourceFreemarkerTemplateLoaderTest extends BaseSharedContextTestCase// AbstractJUnit38SpringContextTests
//{
//    @Autowired
//    private Configuration configuration;
//
//    public void testTemplateLoad() throws Exception
//    {
//        //        Locale german = new Locale("de", "DE");
//        //        Template tpl = configuration.getTemplate("data/hello.ftl", german);
//        Template tpl = configuration.getTemplate("data/hello.ftl");
//        Map<String, Object> root = new HashMap<String, Object>();
//        root.put("name", "Jörg");
//        String result = FreeMarkerTemplateUtils.processTemplateIntoString(tpl, root);
//        assertEquals("Hello Jörg!", result);
//    }
//}
