package org.otherobjects.cms.model;

import junit.framework.TestCase;

public class CmsImageTest extends TestCase
{

    public void testGetMimeType()
    {
        CmsImage im = new CmsImage();
        
        im.setOriginalFileName("fred.jpg");
        assertEquals("image/jpeg", im.getMimeType());
        
        im.setOriginalFileName("fred");
        assertEquals("unknown", im.getMimeType());
    }

}
