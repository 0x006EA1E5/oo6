package org.otherobjects.cms.servlet;

import java.util.Arrays;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;

public class TransparentRequestContentAccessorTest extends TestCase
{

    String contentString;
    byte[] content;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        contentString = "This is some test content";
        content = contentString.getBytes(TransparentRequestContentAccessor.getDefaultEncoding());
    }

    public void testGetInputStream() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(content);

        TransparentRequestContentAccessor trca = new TransparentRequestContentAccessor(request);

        // read all of the input stream
        byte[] body = new byte[content.length];
        trca.getInputStream().read(body, 0, body.length);
        assertTrue(Arrays.equals(content, body));

        // can we read it again
        byte[] body2 = new byte[content.length];
        trca.getInputStream().read(body2, 0, body2.length);
        assertTrue(Arrays.equals(content, body2));

        try
        {
            trca.getReader();
            fail();
        }
        catch (IllegalStateException e)
        {
        }
    }

    public void testGetReader() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(content);

        TransparentRequestContentAccessor trca = new TransparentRequestContentAccessor(request);

        char[] body = new char[contentString.length()];
        trca.getReader().read(body, 0, body.length);
        assertEquals(contentString, new String(body));

        char[] body2 = new char[contentString.length()];
        trca.getReader().read(body2, 0, body2.length);
        assertEquals(contentString, new String(body2));

        try
        {
            trca.getInputStream();
            fail();
        }
        catch (IllegalStateException e)
        {
        }
    }

}
