package org.otherobjects.cms.binding;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;

public class DynaBindingTest2 extends TestCase
{
    public void testDynaNodePathPEditors() throws Exception
    {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter("data[dateOfBirth]", "2001-03-15");
        req.addParameter("data[count]", "1");
        req.addParameter("id", "1");

        DynaNode dn = new DynaNode();

        ServletRequestDataBinder binder = new ServletRequestDataBinder(dn);

        binder.registerCustomEditor(Date.class, "data[dateOfBirth]", new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
        binder.registerCustomEditor(Integer.class, "data[count]", new CustomNumberEditor(Integer.class, false));

        binder.bind(req);

        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2001-03-15"), dn.get("dateOfBirth"));
        assertEquals(new Integer(1), dn.get("count"));
        assertEquals("1", dn.getId());
    }
}
