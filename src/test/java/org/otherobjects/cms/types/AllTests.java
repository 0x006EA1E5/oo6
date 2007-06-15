package org.otherobjects.cms.types;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase
{
    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite("TypeDef Tests");
        suite.addTestSuite(TypeServiceTest.class);
        return suite;
    }
}
