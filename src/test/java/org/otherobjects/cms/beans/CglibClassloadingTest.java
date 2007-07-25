package org.otherobjects.cms.beans;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import junit.framework.TestCase;
import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.model.DynaNode;

public class CglibClassloadingTest extends TestCase {
	
	public void testTwoBeansSameClassname() 
	{
		Object bean1 = getDynaNodeBean();
		Object bean2 = getDynaNodeBean();
		
		
		String genClassName = bean1.getClass().getName();
		System.out.println("Name: " + genClassName);
		assertEquals(bean1.getClass().getName(), bean2.getClass().getName());
		
		
		try {
			Class<?> clazz = Class.forName(genClassName);
			Object bean3 = clazz.newInstance();
			PropertyUtils.setSimpleProperty(bean3, "testString", "This is a test");
		} catch (ClassNotFoundException e) {
			fail("We should find this class as we just added it to the classloader");
		} catch (InstantiationException e) {
			fail();
		} catch (IllegalAccessException e) {
			fail();
		} catch (InvocationTargetException e) {
			fail();
		} catch (NoSuchMethodException e) {
			fail();
		}
		
		
		InputStream in = getClass().getClassLoader().getResourceAsStream("/" + genClassName);
		
		System.out.println(in);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
        try
        {
            byte buffer[] = new byte[2048];
            int len = buffer.length;
            while (true)
            {
                len = in.read(buffer);
                if (len == -1)
                    break;
                out.write(buffer, 0, len);
            }
            out.flush();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
//            if (in != null)
//                in.close();
        }
        
        System.out.println(out.toByteArray());
	
	}
	
	private Object getDynaNodeBean()
	{
		BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.setSuperclass(DynaNode.class);
        
        beanGenerator.addProperty("testString", String.class);
        beanGenerator.addProperty("testLong", Long.class);
        beanGenerator.addProperty("testDate", Date.class);
        return beanGenerator.create();
	}
	
//	private Object getDynaNodeBean2()
//	{
//		BeanGenerator beanGenerator = new BeanGenerator();
//        beanGenerator.setSuperclass(DynaNode.class);
//        
//        beanGenerator.addProperty("testString", String.class);
//        beanGenerator.addProperty("testDate", Date.class);
//        beanGenerator.addProperty("testLong", Long.class);
//        beanGenerator.addProperty("testInt", Integer.class);
//        
//        return beanGenerator.create();
//	}
			
}
