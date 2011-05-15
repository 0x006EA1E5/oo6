package org.otherobjects.cms.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JsonViewTest {
    
    private static Logger logger = LoggerFactory.getLogger(JsonViewTest.class);

    private StaticMessageSource testMessageSource = new StaticMessageSource();

    @Resource
    private OtherObjectsConfigurator otherObjectsConfigurator;
    
    @Resource
    private JsonView jsonView;
    
    @Before
    public void setUp() throws Exception
    {
        testMessageSource.addMessage("something.label", Locale.UK, "SOMETHING LABEL");
        testMessageSource.addMessage("something.else.label", Locale.UK, "SOMETHING ELSE LABEL");
    }

    @Test
    public void testLocaliseString()
    {
        String s1 = "No localisation needed.";
        String s1r = "No localisation needed.";
        String s2 = "${something.label}.";
        String s2r = "SOMETHING LABEL.";
        String s3 = "Here is ${something.label} and ${something.else.label}";
        String s3r = "Here is SOMETHING LABEL and SOMETHING ELSE LABEL";

        JsonView jsonView = new JsonView();

        assertEquals(s1r, jsonView.localiseString(s1, testMessageSource, Locale.UK));
        assertEquals(s2r, jsonView.localiseString(s2, testMessageSource, Locale.UK));
        assertEquals(s3r, jsonView.localiseString(s3, testMessageSource, Locale.UK));

    }

    @Test
    public void testRenderMergedOutputModel() throws Exception
    {
        // FIXME Use StaticApplicationContext for test
        //        StaticApplicationContext ac = new StaticApplicationContext();
        //        ac.registerSingleton("jsonView", JsonView.class);
        //        ac.getBeanFactory().initializeBean(null, "jsonView");
        //        JsonView jsonView = (JsonView) ac.getBean("jsonView");
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> model = new HashMap<String, Object>();


        // Whole map
        model.put("item", "v1");
        jsonView.render(model, request, response);
        String output = response.getContentAsString();
        
        logger.info(output);
        
        assertTrue(output.startsWith("{")); // Should be an object
        assertTrue(output.contains("v1"));

        // Partial data
        List<String> list = Arrays.asList(new String[]{"A1", "B1", "C1"});
        model.put(JsonView.JSON_DATA_KEY, list);
        response = new MockHttpServletResponse();
        
        jsonView.render(model, request, response);
        
        output = response.getContentAsString();
        logger.info(output);
        
        assertTrue(response.getContentAsString().contains("[\"A1\",\"B1\",\"C1\"]"));
    }

}
