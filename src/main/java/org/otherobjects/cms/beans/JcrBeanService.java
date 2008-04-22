//package org.otherobjects.cms.beans;
//
//import org.otherobjects.cms.OtherObjectsException;
//import org.otherobjects.cms.types.TypeDef;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Service class to beanify DynaNodes. Dynamically adds bean style getters and setters for all (simple) properties
// * defined in TypeDef for a given DynaNode {@link JcrBeanService#createCustomDynaNodeClass(DynaNode)}. 
// *  
// * @author joerg
// *
// */
//public class JcrBeanService
//{
//    protected final Logger logger = LoggerFactory.getLogger(getClass());
//
//    /**
//     * Uses CGLIB to generate a custom DynaNode with bean style properties as defined by the given typeDef.
//     * Used for its side effect of actually generating the bytecode for this dynamic class and injecting it into the current classloader.
//     * @param typeDef - typeDef to serve as a template for the dynamic DynaNode to be created
//     * 
//     * @return String - Class name of the generated dynamic class
//     */
//    public String createCustomDynaNodeClass(TypeDef typeDef)
//    {
//        try
//        {
//            BeanCreator creator = new BeanCreator();
//            Object createBean = creator.createBean(typeDef);
//            return createBean.getClass().getName();
//        }
//        catch (Exception e)
//        {
//            throw new OtherObjectsException("Could not create bean class for: " + typeDef.getName(), e);
//        }
//    }
//
//    public boolean hasExistingClass(String className)
//    {
//        try
//        {
//            Class.forName(className);
//            //ClassReader classReader = new ClassReader(className);
//            return true;
//        }
//        catch (Exception e)
//        {
//            return false;
//        }
//    }
//}
