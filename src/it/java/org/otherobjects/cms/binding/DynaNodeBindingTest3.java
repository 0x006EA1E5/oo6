package org.otherobjects.cms.binding;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefImpl;
import org.otherobjects.cms.types.TypeServiceImpl;
import org.springframework.mock.web.MockHttpServletRequest;

public class DynaNodeBindingTest3 extends TestCase
{
    public void testDynaNodeBinding() throws Exception
    {
        TypeServiceImpl ts = new TypeServiceImpl();
        ts.reset();
        TypeDefImpl td = new TypeDefImpl();
        td.setTypeService(ts);
        td.addProperty(new PropertyDefImpl("dateOfBirth", "date", null, null));
        td.addProperty(new PropertyDefImpl("count", "number", null, null));
        td.addProperty(new PropertyDefImpl("surname", "string", null, null));

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter("id", "1");
        req.addParameter("data[dateOfBirth]", "12 03 1973");
        req.addParameter("data[\"count\"]", "11");
        req.addParameter("data[surname]", "miller");

        DynaNode dn = new DynaNode();
        BindServiceImpl bindService = new BindServiceImpl();
        bindService.setDateFormat("dd MM yyyy");

        bindService.bind(dn, td, req);

        assertEquals("1", dn.getId());
        assertEquals(new SimpleDateFormat("dd MM yyyy").parse("12 03 1973"), dn.get("dateOfBirth"));
        assertEquals("miller", dn.get("surname"));
        System.out.println(dn.get("count").getClass().getName());
        assertEquals(new Long(11), dn.get("count"));

    }

    public void testDynaNodePattern()
    {
        String DYNA_NODE_MAP_NAME = "data";

        Pattern pattern = Pattern.compile("^(?:([\\S&&[^\\.]]*)\\.)?" + DYNA_NODE_MAP_NAME + "\\[\"?(.*?)\"?+\\]"); //

        Matcher matcher = pattern.matcher("data[dateOfBirth]");
        assertTrue(matcher.lookingAt());
    }
}
