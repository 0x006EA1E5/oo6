package org.otherobjects.cms.views;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

import org.springframework.context.support.StaticMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class JsonViewTest extends TestCase
{
    private StaticMessageSource testMessageSource = new StaticMessageSource();

    @Override
    protected void setUp() throws Exception
    {
        testMessageSource.addMessage("something.label", Locale.UK, "SOMETHING LABEL");
        testMessageSource.addMessage("something.else.label", Locale.UK, "SOMETHING ELSE LABEL");
        super.setUp();
    }

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

    @SuppressWarnings("unchecked")
    public void testRenderMergedOutputModel() throws Exception
    {
        // FIXME Use StaticApplicationContext for test
        //        StaticApplicationContext ac = new StaticApplicationContext();
        //        ac.registerSingleton("jsonView", JsonView.class);
        //        ac.getBeanFactory().initializeBean(null, "jsonView");
        //        JsonView jsonView = (JsonView) ac.getBean("jsonView");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map model = new HashMap();

        JsonView jsonView = new JsonView();

        // Whole map
        model.put("p1", "v1");
        jsonView.render(model, request, response);
        String output = response.getContentAsString();
        assertTrue(output.startsWith("{")); // Should be an object
        assertTrue(output.contains("v1"));

        // Partial data
        List list = Arrays.asList(new String[]{"A1", "B1", "C1"});
        model.put(JsonView.JSON_DATA_KEY, list);
        response = new MockHttpServletResponse();
        jsonView.render(model, request, response);
        output = response.getContentAsString();
        assertTrue(output.startsWith("[")); // Should be a list
        assertTrue(response.getContentAsString().contains("A1"));
    }

}
