package org.otherobjects.cms.rules;

public interface RuleExecutor
{
    public Object[] runInStatelessSession(Object[] facts, Class classOfResultToReturn);
}
