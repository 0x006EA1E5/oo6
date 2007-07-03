package org.otherobjects.cms.dao;

import org.otherobjects.cms.hibernate.GenericDaoHibernate;
import org.otherobjects.cms.model.SecureObject1;

public class SecureObject1DaoHibernate extends GenericDaoHibernate<SecureObject1, Long> implements SecureObject1Dao {
	
	public SecureObject1DaoHibernate() {
		super(SecureObject1.class);
		
	}

	

}
