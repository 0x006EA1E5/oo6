package org.otherobjects.cms;

import junit.framework.TestCase;

public class OtherObjectsExceptionTest extends TestCase
{
    public void testOtherObjectsException()
    {
        OtherObjectsException e = new OtherObjectsException("error");
        assertEquals("error", e.getMessage());
        
        IllegalArgumentException a = new IllegalArgumentException();
        e = new OtherObjectsException("error", a);
        assertEquals(a, e.getCause());
    }
}
