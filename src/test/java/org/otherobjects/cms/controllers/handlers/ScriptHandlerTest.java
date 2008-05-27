package org.otherobjects.cms.controllers.handlers;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.otherobjects.cms.model.ScriptResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ScriptHandlerTest extends TestCase
{

    private boolean scriptDirExisted = false;
    private File scriptFolder = new File("scripts");

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        if (!scriptFolder.isDirectory() && scriptFolder.exists())
            throw new RuntimeException("Test can not be run if the path 'scripts' exists and is not a directory");

        if (scriptFolder.exists())
            scriptDirExisted = true;
        else
            scriptFolder.mkdir();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        if (!scriptDirExisted)
            scriptFolder.delete();
    }

    public void testRunScript()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        File script = null;
        try
        {
            script = new File("scripts/test.groovy");
            FileUtils.writeStringToFile(script, "response.addHeader('test', 'test');");

            request.addParameter("script", "test");

            ScriptHandler scriptHandler = new ScriptHandler();
            scriptHandler.setDaoService(null);

            ScriptResource scriptResource = new ScriptResource();

            scriptHandler.handleRequest(scriptResource, request, response);

            assertEquals("test", response.getHeader("test"));

        }
        catch (Exception e)
        {
            // TODO Explain why we ignore exception
        }
        finally
        {
            if (script != null)
                script.delete();
        }

    }
}
