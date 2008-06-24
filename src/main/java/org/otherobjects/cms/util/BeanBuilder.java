/*
 * Created on May 19, 2004
 */
package org.otherobjects.cms.util;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.MissingPropertyException;
import groovy.util.BuilderSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BeanBuilder employs the Groovy Markup syntax to enable a tree of Javabean, or GroovyBean instances
 * to be created and wired together.
 * <p>
 * The following example illustrates BeanBuilder markup in a groovy script:
 * <pre>
 * 
 *      builder = new BeanBuilder(this)
 *      Object o = builder.bean {
 *    		beanInstance {
 *      			mapInstance {
 *      				key1 {
 *      					bean {
 *      					}
 *      				}
 *      				key2 {
 *      					list {
 *      					}
 *      				}
 *      		}
 *      	}
 * </pre>
 * <p>
 * Each node name (bean, mapInstance, etc.) is handled according to the type of it's containing element.
 * If the containing element is a bean then the builder looks for a setXXX() method or an addXXX() method on the 
 * containing instance. If one of these methods is found the builder creates an instance of the class of the method
 * argument
 *      
 * @author mhenderson@behindthesite.com
 *
 */
@SuppressWarnings("unchecked")
public class BeanBuilder extends BuilderSupport
{
    private final Logger log = LoggerFactory.getLogger(BeanBuilder.class);

    private List stack = new ArrayList();

    private Map namedBeanElements = new HashMap();

    private Object delegate;

    public BeanBuilder()
    {
        super();
        _init(this);
    }

    public BeanBuilder(Object arg0)
    {
        super();
        _init(arg0);
    }

    private void _init(Object object)
    {
        delegate = object;
        StackElement element = createStackElement(null, null, null, object.getClass(), object, Collections.EMPTY_MAP);
        push(element);
    }

    protected void setParent(Object arg0, Object arg1)
    {
    }

    private String getStackDescription()
    {
        if (stack.size() < 2)
        {
            return "empty";
        }
        Iterator iterator = stack.iterator();
        StackElement e = (StackElement) iterator.next();
        e = (StackElement) iterator.next();
        StringBuffer sb = new StringBuffer(e.property);
        while (iterator.hasNext())
        {
            e = (StackElement) iterator.next();
            sb.append(".");
            sb.append(e.property);
        }
        return sb.toString();
    }

    protected Object getDelegate()
    {
        return delegate;
    }

    protected Object createNode(Object name)
    {

        return createNode(name, Collections.EMPTY_MAP);
    }

    protected Object createNode(Object name, Object arg1)
    {

        return createNode(name, Collections.EMPTY_MAP);
    }

    /**
     * 
     * 
     * 
     * 
     */
    protected Object createNode(Object name, Map map)
    {
        log.trace("About to createNode for name:" + name);

        log.trace("stack = " + getStackDescription());
        Object node = null;
        String property = (String) name;
        Class clazz = null;
        MetaMethod method = null;
        StackElement target = peek();
        log.trace("target = " + target);
        StackElement element = target.createPropertyElement(property, map);
        log.trace("element.bean = " + element.bean);
        log.trace("element.beanClass = " + element.beanClass);
        element.setBeanProperties(map);
        log.trace("element.bean = " + element.bean);
        push(element);
        node = element.getBeanValue();
        return node;
    }

    protected Class resolveClassFromArguments(Map map)
    {
        Class defined = null;
        Object classDefined = map.remove("class");
        if (classDefined != null)
        {
            if (classDefined instanceof Class)
            {
                defined = (Class) classDefined;
            }
            else if (classDefined instanceof String)
            {
                defined = getClassNamed((String) classDefined);
                if (defined == null)
                {
                    throw new IllegalArgumentException("Unknown class '" + classDefined + "' defined at: " + getStackDescription());
                }
            }
            else
            {
                throw new IllegalArgumentException("Illegal value for class: at: " + getStackDescription());
            }
        }
        return defined;
    }

    protected Class resolveClass(Class clazz, Map map)
    {
        Class defined = resolveClassFromArguments(map);
        return (defined == null) ? clazz : defined;
    }

    private Class getClassNamed(String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch (Exception ex)
        {

        }
        return null;
    }

    protected void nodeCompleted(Object parent, Object node)
    {
        log.trace("nodeCompleted----------------------------------");
        log.trace("stack = " + getStackDescription());
        StackElement element = pop();
        log.trace("element = " + element.property);
        StackElement parentElement = peek();
        if (parentElement != null)
        {
            log.trace("parentElement = " + parentElement.property);
            parentElement.set(element);
        }
    }

    protected MetaMethod findSetterMethod(Object object, String name)
    {

        MetaMethod method = findMethod(object, "set" + capitalize(name), void.class, 1);
        if (method == null)
        {
            method = findMethod(object, "add" + capitalize(name), void.class, 1);
        }
        return method;
    }

    protected MetaMethod findCreatorMethod(Object object, String name)
    {

        return findMethod(object, "create" + capitalize(name), null, 0);

    }

    protected Object createInstanceOfClass(Class clazz)
    {
        Object object = null;
        try
        {
            if (clazz.isArray())
            {
                object = new ArrayList();
            }
            else if (clazz.isInterface())
            {
                object = createInterfaceInstance(clazz);
            }
            else
            {
                object = clazz.newInstance();
            }
        }
        catch (InstantiationException ex)
        {

        }
        catch (IllegalAccessException ex)
        {

        }
        return object;
    }

    protected Object createInterfaceInstance(Class clazz)
    {
        String className = clazz.getName();
        if (clazz == List.class || clazz == Collection.class)
        {
            return new ArrayList();
        }
        else if (clazz == Map.class)
        {
            return new HashMap();
        }
        else if (clazz == Set.class)
        {
            return new HashSet();
        }
        return null;
    }

    protected Object createInstanceFromArguments(Map map)
    {
        Object object = null;
        String className = (String) map.remove("class");
        if (className != null)
        {
            try
            {
                Class clazz = Class.forName(className);
                if (clazz != null)
                {
                    object = clazz.newInstance();
                }
            }
            catch (Exception ex)
            {
                // report it!
            }
        }
        return object;
    }

    protected MetaMethod findMethod(Object object, String methodName, Class returnType, int numArgs)
    {
        MetaClass metaClass = InvokerHelper.getMetaClass(object);
        log.trace("findMethod() object     = " + object);
        log.trace("findMethod() methodName = " + methodName);
        log.trace("findMethod() metaClass  = " + metaClass);
        List methods = metaClass.getMethods();
        log.trace("findMethod() methods  = " + methods);
        if (methods != null)
        {
            Iterator iterator = methods.iterator();
            while (iterator.hasNext())
            {
                MetaMethod method = (MetaMethod) iterator.next();
                log.error("findMethod() " + method + " method.getReturnType()  = " + method.getReturnType());
                if (returnType == null || method.getReturnType() == returnType)
                {
                    Class argumentClasses[] = method.getNativeParameterTypes();
                    log.trace("findMethod() argumentClasses  = " + argumentClasses);
                    log.trace("findMethod() argumentClasses.length  = " + argumentClasses.length);
                    if (method.getName().equals(methodName) && argumentClasses.length == numArgs)
                    {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    private Object createInstanceForProperty(String property)
    {
        Object[] creators = new Object[]{peek().getBeanValue(), getDelegate(), this};
        Object object = null;
        Object creator = null;
        MetaMethod method = null;
        for (int i = 0; i < creators.length; i++)
        {
            log.trace("i = " + i);
            creator = creators[i];
            log.trace("creator = " + creator);
            method = findCreatorMethod(creator, property);
            if (method != null)
            {
                object = invokeMethod(creator, method, new Object[]{});
                break;
            }
        }
        log.trace("object = " + object);
        return object;

    }

    private Object invokeMethod(Object target, MetaMethod method, Object[] args)
    {
        Object object = null;
        try
        {
            object = method.invoke(target, args);
        }
        catch (Exception ex)
        {

        }
        return object;
    }

    private String capitalize(String string)
    {
        return string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
    }

    protected Object createNode(Object name, Map map, Object object)
    {

        return createNode(name, map);
    }

    private void push(StackElement element)
    {

        stack.add(element);
    }

    private StackElement peek()
    {
        return (StackElement) stack.get(stack.size() - 1);
    }

    private StackElement pop()
    {
        return (StackElement) stack.remove(stack.size() - 1);
    }

    public Map createMap()
    {
        return new HashMap();

    }

    public List createList()
    {
        return new ArrayList();
    }

    public Set createSet()
    {
        return new HashSet();
    }

    public Collection createCollection()
    {
        return createList();
    }

    private StackElement createStackElement(StackElement parent, String property, MetaMethod setter, Class clazz, Object object, Map map)
    {
        log.trace("clazz = " + clazz);
        StackElement element = null;
        String nameRef = (String) map.remove("bbnameref");
        if (nameRef != null)
        {
            element = new BeanReferenceElement(nameRef);
        }
        else if (clazz != null && clazz.isArray())
        {

            element = new ArrayElement();

        }
        else if (clazz == Object.class)
        {

            element = new ObjectElement();

        }
        else if (object instanceof GroovyObject)
        {

            element = new GroovyBeanElement();

        }
        else if (object instanceof Map)
        {

            element = new MapElement();

        }
        else if (object instanceof Map.Entry)
        {

            element = new MapEntryElement();

        }
        else if (object instanceof Collection)
        {

            element = new CollectionElement();

        }
        else
        {

            element = new BeanElement();
        }
        String name = (String) map.remove("bbname");
        if (name != null)
        {
            namedBeanElements.put(name, element);
            element.setName(name);
        }

        element.setParent(parent);
        element.setProperty(property);
        element.setSetter(setter);
        element.setBeanClass(clazz);
        element.setBean(object);

        return element;
    }

    private abstract class StackElement
    {

        private String name;
        private StackElement parent;
        private String property;
        private MetaMethod setter;
        private Class beanClass;
        private Object bean;

        boolean hasName()
        {
            return getName() != null;
        }

        String getName()
        {
            return name;
        }

        void setName(String value)
        {
            name = value;
        }

        StackElement getParent()
        {
            return parent;
        }

        void setParent(StackElement value)
        {
            parent = value;
        }

        String getProperty()
        {
            return property;
        }

        void setProperty(String value)
        {
            property = value;
        }

        MetaMethod getSetter()
        {
            return setter;
        }

        void setSetter(MetaMethod value)
        {
            setter = value;
        }

        Class getBeanClass()
        {
            return beanClass;
        }

        void setBeanClass(Class value)
        {
            beanClass = value;
        }

        Object getBean()
        {
            return bean;
        }

        void setBean(Object value)
        {
            bean = value;
        }

        void setBeanProperties(Map map)
        {

            for (Iterator iter = map.entrySet().iterator(); iter.hasNext();)
            {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                Object value = entry.getValue();
                setBeanProperty(name, ((value == null) ? null : value.toString()));
            }
        }

        void setBeanProperty(String name, Object value)
        {
            try
            {
                InvokerHelper.setProperty(getBeanValue(), name, value);
            }
            catch (MissingPropertyException mpe)
            {
                if (stack.size() > 1)
                {
                    throw mpe;
                }
            }
        }

        abstract void set(StackElement element);

        abstract StackElement createPropertyElement(String property, Map map);

        Object getBeanValue()
        {
            return bean;
        }

    }

    private class ObjectElement extends StackElement
    {

        void set(StackElement propertyElement)
        {
            throw new IllegalArgumentException("Cannot set properties on java.lang.Object at: " + getStackDescription());
        }

        StackElement createPropertyElement(String property, Map map)
        {
            throw new IllegalArgumentException("Cannot set properties on java.lang.Object at: " + getStackDescription());
        }

    }

    private class MapElement extends StackElement
    {

        void set(StackElement propertyElement)
        {

            Map.Entry entry = (Map.Entry) propertyElement.getBeanValue();
            ((Map) getBean()).put(entry.getKey(), entry.getValue());
        }

        void setBeanProperty(String name, Object value)
        {
            Map map = (Map) getBean();
            map.put(name, value);
        }

        StackElement createPropertyElement(String property, Map map)
        {
            MetaMethod method = null;
            Class clazz = null;
            Object node = null;
            if (property.equals("entry"))
            {
                if (map.get("key") == null)
                {
                    throw new IllegalArgumentException("Map at:  " + getStackDescription() + " entry requires a key");
                }
                node = new Entry();
            }
            else
            {
                Entry entry = new Entry();
                entry.setKey(property);
                node = entry;
            }
            return createStackElement(this, property, method, clazz, node, map);
        }
    }

    private class MapEntryElement extends StackElement
    {

        void set(StackElement propertyElement)
        {
            Entry entry = (Entry) getBean();
            entry.setValue(propertyElement.getBeanValue());
        }

        StackElement createPropertyElement(String property, Map map)
        {
            MetaMethod method = null;
            Class clazz = null;
            Object node = null;
            if (property.equals("value") || property.equals("key"))
            {
                clazz = resolveClassFromArguments(map);
                if (clazz != null)
                {
                    node = createInstanceOfClass(clazz);
                }
            }
            else
            {
                node = createInstanceForProperty(property);
            }
            return createStackElement(this, property, method, clazz, node, map);
        }
    }

    private class CollectionElement extends StackElement
    {

        void set(StackElement propertyElement)
        {
            try
            {
                Collection collection = (Collection) getBean();
                collection.add(propertyElement.getBeanValue());
            }
            catch (Exception ex)
            {

            }
        }

        StackElement createPropertyElement(String property, Map map)
        {
            MetaMethod method = null;
            Class clazz = null;
            Object node = null;
            if (property.equals("element"))
            {
                clazz = resolveClassFromArguments(map);
                if (clazz != null)
                {
                    node = createInstanceOfClass(clazz);
                }
            }
            else
            {
                node = createInstanceForProperty(property);
            }
            return createStackElement(this, property, method, clazz, node, map);
        }
    }

    private class BeanElement extends StackElement
    {

        void set(StackElement propertyElement)
        {
            try
            {
                propertyElement.setter.invoke(getBeanValue(), new Object[]{propertyElement.getBeanValue()});
            }
            catch (Exception ex)
            {
                if (!(stack.size() > 1))
                {
                    throw new MissingPropertyException(propertyElement.getProperty(), getBeanClass());
                }

            }
        }

        StackElement createPropertyElement(String property, Map map)
        {
            MetaMethod method = findSetterMethod(getBean(), property);
            Class clazz = null;
            Object node = null;
            if (method != null)
            {
                clazz = method.getNativeParameterTypes()[0];
                clazz = resolveClass(clazz, map);
                node = createInstanceOfClass(clazz);
            }
            else
            {
                node = createInstanceForProperty(property);
            }
            return createStackElement(this, property, method, clazz, node, map);
        }

    }

    private class GroovyBeanElement extends BeanElement
    {

        void set(StackElement propertyElement)
        {
            try
            {
                GroovyObject gObject = (GroovyObject) getBean();
                String propertyElementProperty = propertyElement.getProperty();
                Object propertyElementBeanValue = propertyElement.getBeanValue();
                log.trace("GroovyBeanElement.set(" + propertyElementProperty + ". " + propertyElementBeanValue + ")");
                gObject.setProperty(propertyElementProperty, propertyElementBeanValue);
            }
            catch (MissingPropertyException mpe)
            {
                if (stack.size() > 1)
                {
                    throw mpe;
                }
            }
        }
    }

    private class ArrayElement extends CollectionElement
    {

        void set(StackElement propertyElement)
        {
            ((List) getBean()).add(propertyElement.getBeanValue());
        }

        Object getBeanValue()
        {
            return ((List) getBean()).toArray();
        }
    }

    private class BeanReferenceElement extends StackElement
    {
        private String bbRefName;
        private StackElement referencedElement;

        BeanReferenceElement(String name)
        {
            bbRefName = name;
            referencedElement = (StackElement) namedBeanElements.get(bbRefName);
            if (referencedElement == null)
            {
                throw new IllegalStateException("No bean with bbName of " + bbRefName + "available for bean at: " + getStackDescription());
            }
        }

        public void set(StackElement propertyElement)
        {
            throw new IllegalStateException("Bean References cannot set bean properties: " + getStackDescription());
        }

        StackElement createPropertyElement(String property, Map map)
        {
            throw new IllegalStateException("Bean References cannot set bean properties: " + getStackDescription());
        }

        void setReferencedElement(StackElement element)
        {

        }

        Object getBeanValue()
        {
            return referencedElement.getBeanValue();
        }
    }

    class Entry implements Map.Entry
    {
        private Object key;
        private Object value;

        public Object getKey()
        {
            return key;
        }

        public void setKey(Object key)
        {
            this.key = key;
        }

        public Object getValue()
        {
            return value;
        }

        public Object setValue(Object value)
        {
            Object old = value;
            this.value = value;
            return old;
        }

        public boolean equals(Object anObject)
        {
            boolean result = (anObject instanceof Map.Entry);
            if (result)
            {
                Map.Entry other = (Map.Entry) anObject;
                result = (other.getKey().equals(key) && other.getValue().equals(value));
            }
            return result;
        }

        public int hashCode()
        {
            return (key == null) ? 0 : key.hashCode();
        }
    }
}
