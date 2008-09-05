package org.otherobjects.cms.binding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import junit.framework.TestCase;
import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * Tests to examine workings of Spring's standard bindings.
 * 
 * @author joerg
 */
@SuppressWarnings("unchecked")
public class SpringBindingTest extends TestCase
{

    private MockHttpServletRequest request;
    //private MockHttpServletResponse response;
    private BeanGenerator commandObjectGenerator;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        request = new MockHttpServletRequest();
        //response = new MockHttpServletResponse();

        commandObjectGenerator = new BeanGenerator();
        commandObjectGenerator.addProperty("name", String.class);
        commandObjectGenerator.addProperty("dob", Date.class);
        commandObjectGenerator.addProperty("id", Long.class);
    }

    public void testStringBind() throws Exception
    {
        Object myBean = commandObjectGenerator.create();

        request.addParameter("name", "test");
        ServletRequestDataBinder binder = new ServletRequestDataBinder(myBean);
        binder.bind(request);

        System.out.println("Errors:");
        for (Iterator it = binder.getBindingResult().getAllErrors().iterator(); it.hasNext();)
        {
            System.out.println(it.next());
        }

        assertEquals("test", PropertyUtils.getSimpleProperty(myBean, "name"));
    }

    public void testDateBind() throws Exception
    {
        GregorianCalendar cal = new GregorianCalendar(2007, 3, 30);

        Object myBean = commandObjectGenerator.create();

        request.addParameter("dob", "30-04-2007");
        ServletRequestDataBinder binder = new ServletRequestDataBinder(myBean);
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(new SimpleDateFormat("dd-MM-yyyy"), true));
        binder.bind(request);

        System.out.println("Errors:");
        for (Iterator it = binder.getBindingResult().getAllErrors().iterator(); it.hasNext();)
        {
            System.out.println(it.next());
        }

        assertEquals(cal.getTime(), PropertyUtils.getSimpleProperty(myBean, "dob"));
    }

    public void testLongBind() throws Exception
    {
        Object myBean = commandObjectGenerator.create();

        request.addParameter("id", "17");
        ServletRequestDataBinder binder = new ServletRequestDataBinder(myBean);
        binder.bind(request);

        System.out.println("Errors:");
        for (Iterator it = binder.getBindingResult().getAllErrors().iterator(); it.hasNext();)
        {
            System.out.print(it.next());
        }

        assertEquals(new Long(17), PropertyUtils.getSimpleProperty(myBean, "id"));
    }
    
    public void testBindErrrorsGetSet() throws Exception
    {
        Object myBean = commandObjectGenerator.create();

        request.addParameter("id", "abc");
        ServletRequestDataBinder binder = new ServletRequestDataBinder(myBean);
        binder.bind(request);

        System.out.println("Errors:");
        for (Iterator it = binder.getBindingResult().getAllErrors().iterator(); it.hasNext();)
        {
            System.out.println(it.next());
        }
        
        assertTrue("A binding error should've occurred", binder.getBindingResult().getAllErrors().size() > 0);
        
    }

    public void testInspectionCache() throws Exception
    {
        Object myBean = commandObjectGenerator.create();

        request.addParameter("id", "17");
        ServletRequestDataBinder binder = new ServletRequestDataBinder(myBean);
        binder.bind(request);

        System.out.println("Errors:");
        for (Iterator it = binder.getBindingResult().getAllErrors().iterator(); it.hasNext();)
        {
            System.out.println(it.next());
        }

        assertEquals(new Long(17), PropertyUtils.getSimpleProperty(myBean, "id"));

        // now modify the bean
        commandObjectGenerator.addProperty("id2", Integer.class);
        Object myBean2 = commandObjectGenerator.create();

        request.addParameter("id2", "19");
        ServletRequestDataBinder binder2 = new ServletRequestDataBinder(myBean2);
        binder2.bind(request);

        assertEquals(new Integer(19), PropertyUtils.getSimpleProperty(myBean2, "id2"));

    }
    
    /*public void testDynaNodePathPEditors() throws Exception
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

        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2001-03-15"), dn.getPropertyValue("dateOfBirth"));
        assertEquals(new Integer(1), dn.getPropertyValue("count"));
        assertEquals("1", dn.getId());
    }*/

}
