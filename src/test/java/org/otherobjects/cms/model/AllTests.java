package org.otherobjects.cms.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase
{
    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite("Model Tests");
        suite.addTestSuite(CmsNodeTest.class);
        return suite;
    }
}
