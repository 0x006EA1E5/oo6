package org.otherobjects.cms.hibernate;


public class SecureObject1DaoHibernate extends GenericDaoHibernate<SecureObject1, Long> implements SecureObject1Dao
{
    public SecureObject1DaoHibernate()
    {
        super(SecureObject1.class);
    }
}
