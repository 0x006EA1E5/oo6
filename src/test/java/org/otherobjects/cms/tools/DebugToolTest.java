package org.otherobjects.cms.tools;

import junit.framework.TestCase;

import org.otherobjects.cms.beans.DummyBean;

public class DebugToolTest extends TestCase
{
    private DummyBean testBean;
    private static final String LIPSUM = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas id mi a ligula mollis fermentum. Integer eu sem ac ligula bibendum "
            + "tincidunt. Mauris iaculis tempor sem. Nulla risus lectus, malesuada vitae, eleifend eu, elementum auctor, tortor. Cras id nibh at odio ullamcorper accumsan. "
            + "Morbi non urna eu augue consequat dictum. Curabitur augue massa, molestie eu, ornare vel, blandit vitae, massa. Phasellus nec neque eget neque congue aliquam. "
            + "Donec rhoncus. Mauris posuere, augue sit amet porta viverra, purus libero placerat tortor, vel pretium odio nisi at augue. Nullam vestibulum mi scelerisque dolor. "
            + "Nullam non orci eu dui iaculis volutpat. Suspendisse egestas accumsan leo. Quisque molestie neque. Suspendisse ornare, eros vel venenatis pharetra, "
            + "felis massa consequat tortor, et aliquam ipsum urna ac metus.";

    //static final String lipsum = "Lorem ipsum";

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        this.testBean = new DummyBean();

        this.testBean.setFirstName("Test");
        this.testBean.setLastName("Tester");
        this.testBean.setDescription(LIPSUM);
        this.testBean.setActive(true);
        this.testBean.setId(new Long(1));
        this.testBean.setEmailAddresses(new String[]{"email1@test.com", "email2@test.com"});

        DummyBean childBean1 = new DummyBean();
        childBean1.setFirstName("Test");
        childBean1.setLastName(null);
        childBean1.setDescription(LIPSUM);
        childBean1.setActive(false);
        childBean1.setId(new Long(1));
        childBean1.setEmailAddresses(new String[]{"child1a@test.com", "child1b@test.com"});

        DummyBean childBean2 = new DummyBean();
        childBean2.setFirstName("Test");
        childBean2.setLastName("Tester");
        childBean2.setDescription(LIPSUM);
        childBean2.setActive(true);
        childBean2.setId(new Long(1));
        childBean2.setEmailAddresses(new String[]{"child2a@test.com", "child2b@test.com", "child2c@test.com"});

        DummyBean childBean3 = null;

        this.testBean.addChildDummyBean(childBean1);
        this.testBean.addChildDummyBean(childBean2);
        this.testBean.addChildDummyBean(childBean3);

    }

    public void testToString()
    {
        // Test null handling
        System.out.println(DebugTool.inspectAsText(null, true));

        // Test child beans
        System.out.println(DebugTool.inspectAsText(this.testBean, true));
    }

    public void testToHtml()
    {
        // Test null handling
        System.out.println(DebugTool.inspectAsText(null, true));

        // Test child beans
        System.out.println(DebugTool.inspect(this.testBean));
    }
}
