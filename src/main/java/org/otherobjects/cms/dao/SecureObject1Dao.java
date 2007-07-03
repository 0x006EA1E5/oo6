package org.otherobjects.cms.dao;

import org.otherobjects.cms.model.SecureObject1;

public interface SecureObject1Dao extends GenericDao<SecureObject1, Long> {
	public SecureObject1 get(Long id);
}
