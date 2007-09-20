package org.otherobjects.cms.util;

import java.util.UUID;

import junit.framework.TestCase;

public class IdentifierUtilsTest extends TestCase {
	
	public void testIsUUID()
	{
		try{
			for(int i = 0; i < 500; i++)
			{
				String uuid = UUID.randomUUID().toString();
				if(!IdentifierUtils.isUUID(uuid))
					throw new RuntimeException("Not a UUID");
			}
		}
		catch(RuntimeException e)
		{
			fail();
		}
	}
	
	public void testGetCompositeDatabaseId()
	{
		String dbId1 = "java.lang.String-99";
		String dbId2 = "java.lang.Loo-ng-1025";
		
		assertNotNull(IdentifierUtils.getCompositeDatabaseId(dbId1));
		assertEquals("java.lang.String", IdentifierUtils.getCompositeDatabaseId(dbId1).getClazz());
		assertEquals(99l, IdentifierUtils.getCompositeDatabaseId(dbId1).getId());
		
		assertNull(IdentifierUtils.getCompositeDatabaseId(dbId2));
		
		
	}
}
