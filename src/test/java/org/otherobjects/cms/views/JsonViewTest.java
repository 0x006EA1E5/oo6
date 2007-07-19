package org.otherobjects.cms.views;

import java.util.Locale;

import junit.framework.TestCase;

import org.springframework.context.support.StaticMessageSource;

public class JsonViewTest extends TestCase
{
    private StaticMessageSource testMessageSource = new StaticMessageSource();

    @Override
    protected void setUp() throws Exception
    {
        testMessageSource.addMessage("something.label",Locale.UK,"SOMETHING LABEL");
        testMessageSource.addMessage("something.else.label",Locale.UK,"SOMETHING ELSE LABEL");
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

}
