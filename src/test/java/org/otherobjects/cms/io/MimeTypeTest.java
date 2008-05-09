package org.otherobjects.cms.io;

import javax.activation.MimetypesFileTypeMap;

import junit.framework.TestCase;

public class MimeTypeTest extends TestCase
{
    public void testTemplateFormats()
    {
        MimetypesFileTypeMap mime = new MimetypesFileTypeMap();

        System.out.println("Textfile: " + mime.getContentType("test.ftl"));
    }
}
