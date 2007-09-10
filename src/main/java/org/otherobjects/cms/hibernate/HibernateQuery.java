package org.otherobjects.cms.hibernate;

import groovy.lang.GroovyShell;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.otherobjects.cms.OtherObjectsException;

public class HibernateQuery {

	public final static String[] CLAUSE_KEYS = {"select", "from", "join", "where", "orderBy"};

	/**
	 * Leave empty for simple object queries like 'from org.otherobjects.cms.model.Article ...' or specify scalars to return
	 */
	private String selectClause;
	
	/**
	 * Class to return. Gets aliased to 'o'
	 */
	private String fromClause;
	
	/**
	 * Joined classes to query. Overrides fromClause, i.e. if a joinClause is set fromClause gets ignored.
	 * You need to look after aliasing if using this.
	 */
	private String joinClause;
	
	/**
	 * where criteria
	 */
	private String whereClause;
	
	/**
	 * order by criteria
	 */
	private String orderByClause;

	private Map<String, Object> namedParameters;

	public HibernateQuery()
	{
		this.namedParameters = new HashMap<String, Object>();
	}
	
	/**
	 * Build a HQL query by passing in a groovy style map of hql clauses. For possible clauses see {@link #CLAUSE_KEYS}
	 * 
	 * @param clauses
	 */
	public HibernateQuery(String clauses)
	{
		this();
		GroovyShell shell = new GroovyShell();
		Map<String, String> clausesMap = (Map<String, String>) shell.evaluate("def map = " + clauses + "; return map;");
		try {
			for(int i = 0; i < CLAUSE_KEYS.length; i++)
			{
				String clause = CLAUSE_KEYS[i];

				if(clausesMap.containsKey(clause))
					PropertyUtils.setSimpleProperty(this, clause + "Clause", clausesMap.get(clause));
			}
		} catch (Exception e) {
			//noop
		} 
	}

	public String toHqlString()
	{
		StringBuffer buf = new StringBuffer();
		if(StringUtils.isNotBlank(getSelectClause()))
		{
			buf.append("Select ");
			buf.append(getSelectClause());
			buf.append(" ");
		}
		
		buf.append(getFromWhereClause());
		
		if(StringUtils.isNotBlank(getOrderByClause()))
		{
			buf.append("order by ");
			buf.append(getOrderByClause());
			buf.append(" ");
		}
		
		return buf.toString();
	}
	
	public String toCountHqlString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("Select count(*) ");
		buf.append(getFromWhereClause());
		
		return buf.toString();
	}
	
	private String getFromWhereClause()
	{
		StringBuffer buf = new StringBuffer();
		if(StringUtils.isNotBlank(getJoinClause()))
		{
			buf.append("from ");
			buf.append(getJoinClause());
			buf.append("  ");
		} else if(StringUtils.isNotBlank(getSelectClause()))
		{
			buf.append("from ");
			buf.append(getFromClause());
			buf.append(" as o ");
		} else
			throw new OtherObjectsException("You must either have a from or a joinClause");
		
		if(StringUtils.isNotBlank(getWhereClause()))
		{
			buf.append("where ");
			buf.append(getWhereClause());
			buf.append(" ");
		}
		return buf.toString();
	}
	
	public Query getQuery(Session session)
	{
		Query query = session.createQuery(toHqlString());
		applyParameters(query);
		return query;
	}
	
	public int getRecordCount(Session session)
	{
		Query query = session.createQuery(toCountHqlString());
		applyParameters(query);
		return ( (Integer) (query.iterate().next()) ).intValue();
	}
	
	private void applyParameters(Query query) {
		if(namedParameters.size() <= 0)
			return;
		
		for(Map.Entry<String, Object> entry : namedParameters.entrySet())
		{
			query.setParameter(entry.getKey(), entry.getValue());
		}
	}

	public void addParameter(String name, Object value)
	{
		namedParameters.put(name, value);
	}
	
	public void setParameters(Map<String,Object> namedParameters)
	{
		this.namedParameters = namedParameters;
	}
	
	public void addParameters(Map<String, Object> namedParameters)
	{
		this.namedParameters.putAll(namedParameters);
	}

	public String getSelectClause() {
		return selectClause;
	}

	public void setSelectClause(String selectClause) {
		this.selectClause = selectClause;
	}

	public String getFromClause() {
		return fromClause;
	}

	public void setFromClause(String fromClause) {
		this.fromClause = fromClause;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getOrderByClause() {
		return orderByClause;
	}

	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	public String getJoinClause() {
		return joinClause;
	}

	public void setJoinClause(String joinClause) {
		this.joinClause = joinClause;
	}

	

}
