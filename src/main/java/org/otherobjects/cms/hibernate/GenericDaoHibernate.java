package org.otherobjects.cms.hibernate;

import java.io.Serializable;
import java.util.List;

import org.otherobjects.cms.dao.GenericDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * This class serves as the Base class for all other DAOs - namely to hold
 * common CRUD methods that they might all use. You should only need to extend
 * this class when your require custom CRUD logic.
 *
 * <p>To register this class in your Spring context file, use the following XML.
 * <pre>
 *      &lt;bean id="fooDao" class="org.appfuse.dao.hibernate.GenericDaoHibernate"&gt;
 *          &lt;constructor-arg value="org.appfuse.model.Foo"/&gt;
 *          &lt;property name="sessionFactory" ref="sessionFactory"/&gt;
 *      &lt;/bean&gt;
 * </pre>
 *
 * @author AppFuse <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 */
public class GenericDaoHibernate<T, PK extends Serializable> extends HibernateDaoSupport implements GenericDao<T, PK> {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private Class<T> persistentClass;

    public GenericDaoHibernate(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    @SuppressWarnings("unchecked")
	public List<T> getAll() {
        return super.getHibernateTemplate().loadAll(this.persistentClass);
    }

    @SuppressWarnings("unchecked")
	public T get(PK id) {
        T entity = (T) super.getHibernateTemplate().get(this.persistentClass, id);

        if (entity == null) {
            log.warn("Uh oh, '" + this.persistentClass + "' object with id '" + id + "' not found...");
            throw new ObjectRetrievalFailureException(this.persistentClass, id);
        }

        return entity;
    }
    
    @SuppressWarnings("unchecked")
	public boolean exists(PK id) {
        T entity = (T) super.getHibernateTemplate().get(this.persistentClass, id);
        if (entity == null) {
            return false;
        } else {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
	public T save(T object) {
        return (T) super.getHibernateTemplate().merge(object);
    }

    public void remove(PK id) {
        super.getHibernateTemplate().delete(this.get(id));
    }
}
