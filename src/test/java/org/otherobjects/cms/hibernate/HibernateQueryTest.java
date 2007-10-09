package org.otherobjects.cms.hibernate;

import groovy.lang.GroovyShell;

import java.util.Map;

import junit.framework.TestCase;

import org.otherobjects.cms.model.User;

@SuppressWarnings("unchecked")
public class HibernateQueryTest extends TestCase
{
    public void testClauseMapParsing()
    {
        GroovyShell shell = new GroovyShell();
        String clauses = "[where:'id>0',from:'org.otherobjects.cms.model.Article']";
        Map clauseMap = (Map) shell.evaluate("def map = " + clauses + "; return map;");
        assertEquals("org.otherobjects.cms.model.Article", clauseMap.get("from"));
    }

    public void testClauseConstructor()
    {
        HibernateQuery hq = new HibernateQuery("[select:'o.label', from:'org.otherobjects.cms.model.Article', join:'TEST',where:'o.id > 0', orderBy:'o.name DESC']");
        assertEquals("o.label", hq.getSelectClause());
        assertEquals("org.otherobjects.cms.model.Article", hq.getFromClause());
        assertEquals("TEST", hq.getJoinClause());
        assertEquals("o.id > 0", hq.getWhereClause());
        assertEquals("o.name DESC", hq.getOrderByClause());

        System.out.println(hq.toHqlString());
    }

    public void testToString()
    {
        HibernateQuery hq = new HibernateQuery(User.class);
        hq.setWhereClause("o.username=:name");
        hq.addParameter("name", "test");
        assertTrue(hq.toString().contains("test"));
        System.out.println(hq.toString());
    }

}
