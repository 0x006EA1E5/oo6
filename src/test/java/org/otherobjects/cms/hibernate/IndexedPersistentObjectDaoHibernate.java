package org.otherobjects.cms.hibernate;

public class IndexedPersistentObjectDaoHibernate extends GenericDaoHibernate<IndexedPersistentObject, Long>
		implements IndexedPersistentObjectDao {

	public IndexedPersistentObjectDaoHibernate()
	{
		super(IndexedPersistentObject.class);
	}
}
