//package org.otherobjects.cms.hibernate;
//
//import java.util.Date;
//
//import org.otherobjects.cms.test.BaseDaoTestCase;
//
//public class IndexedPersistenObjectTest extends BaseDaoTestCase {
//	
//	private IndexedPersistentObjectDao indexedPersistentObjectDao;
//	
//	public void setIndexedPersistentObjectDao(
//			IndexedPersistentObjectDao indexedPersistentObjectDao) {
//		this.indexedPersistentObjectDao = indexedPersistentObjectDao;
//	}
//
//	protected String[] getConfigLocations()
//    {
//        setAutowireMode(AUTOWIRE_BY_NAME);
//        return new String[]{"file:src/test/resources/applicationContext-resources.xml", "file:src/test/resources/applicationContext-indexed-dao.xml",
//        		"file:src/main/resources/otherobjects.resources/config/applicationContext-dao.xml",};
//    }
//	
////	public void testIndex()
////	{
////		IndexedPersistentObject ipo = new IndexedPersistentObject();
////		ipo.setTitle("A test Title");
////		ipo.setSummary("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Mauris adipiscing porttitor neque. Fusce aliquet. Etiam euismod adipiscing sapien. Etiam consequat nisi sit amet velit. Nunc luctus. Nam blandit massa at est. Integer pellentesque. Curabitur ultrices tempus risus.");
////		ipo.setDate(new Date());
////		IndexedPersistentObject ipoNew = indexedPersistentObjectDao.save(ipo);
////		
////		System.out.println(ipoNew.getId());
////		
////	}
//}
