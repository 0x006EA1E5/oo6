package org.otherobjects.cms.util;

import java.util.UUID;

import junit.framework.TestCase;

public class IdentifierUtilsTest extends TestCase
{

    private static final int TEST_ITERATIONS = 500;

    public void testIsUUID()
    {
        try
        {
            for (int i = 0; i < TEST_ITERATIONS; i++)
            {
                String uuid = UUID.randomUUID().toString();
                if (!IdentifierUtils.isUUID(uuid))
                    throw new RuntimeException("Not a UUID");
            }
        }
        catch (RuntimeException e)
        {
            fail();
        }
    }

    public void testGetCompositeDatabaseId()
    {
        String dbId1 = "java.lang.String-789";
        String dbId2 = "java.lang.Loo-ng-1025";

        assertNotNull(IdentifierUtils.getCompositeDatabaseId(dbId1));
        assertEquals("java.lang.String", IdentifierUtils.getCompositeDatabaseId(dbId1).getClazz());
        assertEquals(789L, IdentifierUtils.getCompositeDatabaseId(dbId1).getId());

        assertNull(IdentifierUtils.getCompositeDatabaseId(dbId2));

    }
}
