package org.otherobjects.cms.util;

import junit.framework.TestCase;

public class VersionTest extends TestCase
{
    public void testGetVersion()
    {
        Version v1 = Version.getVersion("1.2.3");
        Version v2 = Version.getVersion("2.2.3");
        
        assertEquals(3,v1.getMicro());
        assertTrue(v1.compareTo(v2)<0);
    }
}
