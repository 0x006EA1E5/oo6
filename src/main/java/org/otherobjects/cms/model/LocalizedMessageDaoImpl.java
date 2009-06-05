package org.otherobjects.cms.model;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.otherobjects.cms.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class LocalizedMessageDaoImpl extends GenericDaoHibernate<LocalizedMessage, String> implements LocalizedMessageDao
{
    public LocalizedMessageDaoImpl()
    {
        super(LocalizedMessage.class);
    }
    
    @Resource
    public void initSessionFactory(SessionFactory sessionFactory)
    {
        super.setSessionFactory(sessionFactory);
    }
    
    @Override
    public LocalizedMessage save(LocalizedMessage object)
    {
        return super.save(object);
    }
}
