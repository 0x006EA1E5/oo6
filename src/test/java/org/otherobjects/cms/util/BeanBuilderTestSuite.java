package org.otherobjects.cms.util;

import groovy.util.GroovyTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class BeanBuilderTestSuite extends TestSuite
{
    // Since Eclipse launches tests relative to the project root,
    // declare the relative path to the test scripts for convenience
    private static final String TEST_ROOT = "src/test/java/org/otherobjects/cms/util/";

    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();
        GroovyTestSuite gsuite = new GroovyTestSuite();
//        suite.addTestSuite(FooTest.class); // non-groovy test cases welcome, too.
        suite.addTestSuite(gsuite.compile(TEST_ROOT + "BeanBuilderTest.groovy"));
        return suite;
    }
}
