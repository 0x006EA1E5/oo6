package org.otherobjects.cms.hibernate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.otherobjects.cms.dao.GenericDao;
import org.otherobjects.cms.dao.PagedList;
import org.otherobjects.cms.dao.PagedListImpl;
import org.otherobjects.framework.OtherObjectsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class GenericDaoHibernate<T, PK extends Serializable> extends HibernateDaoSupport implements GenericDao<T, PK>
{
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private Class<T> persistentClass;

    public GenericDaoHibernate(Class<T> persistentClass)
    {
        this.persistentClass = persistentClass;
    }


    public Class<T> getPersistentClass()
    {
        return persistentClass;
    }
    
    public String getPersistentClassName()
    {
        return this.persistentClass.getName();
    }

    public List<T> getAll()
    {
        return super.getHibernateTemplate().loadAll(this.persistentClass);
    }

    public T get(PK id)
    {
        T entity = (T) super.getHibernateTemplate().get(this.persistentClass, id);

        if (entity == null)
        {
            log.warn("Uh oh, '" + this.persistentClass + "' object with id '" + id + "' not found...");
            throw new ObjectRetrievalFailureException(this.persistentClass, id);
        }

        return entity;
    }

    public boolean exists(PK id)
    {
        T entity = (T) super.getHibernateTemplate().get(this.persistentClass, id);
        return (entity != null);
    }

    public T save(T object)
    {
        return save(object, true);
    }

    public T save(T object, boolean validate)
    {
        return (T) super.getHibernateTemplate().merge(object);
    }

    public void remove(PK id)
    {
        super.getHibernateTemplate().delete(this.get(id));
    }

    public PagedList<T> getAllPaged(int pageSize, int pageNo, String filterQuery, String sortField, boolean asc)
    {
        return getPagedByQuery("from " + persistentClass.getName(), pageSize, pageNo, filterQuery, sortField, asc);
    }

    public List<T> findByQuery(final String query, final Map<String, Object> queryParams)
    {
        return findByQuery(query, queryParams, null, null);
    }

    @SuppressWarnings("unchecked")
    public List<T> findByQuery(final String query, final Map<String, Object> queryParams, final Integer start, final Integer limit)
    {
        return getHibernateTemplate().executeFind(new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query q = session.createQuery(query);
                if (queryParams != null)
                {
                    for (String key : queryParams.keySet())
                    {
                        q.setParameter(key, queryParams.get(key));
                    }
                }
                if (limit != null)
                    q.setMaxResults(limit);
                if (start != null)
                    q.setFirstResult(start);
                return q.list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public PagedList<T> getPagedByQuery(final String queryString, final int pageSize, final int pageNo, final String filterQuery, final String sortField, final boolean asc)
    {
        Assert.isTrue(queryString.trim().toLowerCase().startsWith("from"), "Currently only object hql queries are supported - those that start directly with 'from'");

        return (PagedList<T>) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                //FIXME we need to take into account filter and order
                Query query = session.createQuery("SELECT COUNT(*) " + queryString);
                Long count = (Long) query.iterate().next();

                if (count > 0)
                {
                    int startIndex = PagedListImpl.calcStartIndex(pageSize, pageNo);

                    query = session.createQuery(queryString);
                    query.setFirstResult(startIndex);
                    query.setMaxResults(pageSize);

                    List results = query.list();
                    return new PagedListImpl<T>(pageSize, (int) count.longValue(), pageNo, results, false);
                }
                else
                {
                    return new PagedListImpl<T>(pageSize, 0, pageNo, null, false);
                }
            }
        });
    }

    public T create()
    {
        try
        {
            return persistentClass.newInstance();
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Unable to create new class of type: " + persistentClass, e);
        }
    }
}
