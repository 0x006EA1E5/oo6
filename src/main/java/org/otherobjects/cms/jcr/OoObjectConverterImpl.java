//package org.otherobjects.cms.jcr;
//
//import javax.jcr.Node;
//import javax.jcr.Session;
//
//import org.apache.jackrabbit.ocm.mapper.Mapper;
//import org.apache.jackrabbit.ocm.mapper.model.BeanDescriptor;
//import org.apache.jackrabbit.ocm.persistence.atomictypeconverter.AtomicTypeConverterProvider;
//import org.apache.jackrabbit.ocm.persistence.beanconverter.BeanConverter;
//import org.apache.jackrabbit.ocm.persistence.cache.ObjectCache;
//import org.apache.jackrabbit.ocm.persistence.cache.impl.RequestObjectCacheImpl;
//import org.apache.jackrabbit.ocm.persistence.impl.PersistenceUtil;
//import org.apache.jackrabbit.ocm.persistence.objectconverter.ProxyManager;
//import org.apache.jackrabbit.ocm.persistence.objectconverter.impl.ObjectConverterImpl;
//import org.apache.jackrabbit.ocm.persistence.objectconverter.impl.ProxyManagerImpl;
//import org.apache.jackrabbit.ocm.persistence.objectconverter.impl.SimpleFieldsHelper;
//import org.apache.jackrabbit.ocm.reflection.ReflectionUtils;
//
//public class OoObjectConverterImpl extends ObjectConverterImpl
//{
//
//    public OoObjectConverterImpl()
//    {
//    }
//
//    /**
//     * Constructor
//     * 
//     * @param mapper
//     *            The mapper to used
//     * @param converterProvider
//     *            The atomic type converter provider
//     * 
//     */
//    public OoObjectConverterImpl(Mapper mapper, AtomicTypeConverterProvider converterProvider)
//    {
//        super(mapper, converterProvider);
//    }
//
//    /**
//     * Constructor
//     * 
//     * @param mapper
//     *            The mapper to used
//     * @param converterProvider
//     *            The atomic type converter provider
//     * 
//     */
//    public OoObjectConverterImpl(Mapper mapper, AtomicTypeConverterProvider converterProvider, ProxyManager proxyManager, ObjectCache requestObjectCache)
//    {
//        super(mapper, converterProvider, proxyManager, requestObjectCache);
//    }
//    
//    private void retrieveBeanField(Session session,BeanDescriptor beanDescriptor, Node node, String path, Object object, boolean forceToRetrieve )
//    {
//        if (!beanDescriptor.isAutoRetrieve() && !forceToRetrieve) 
//        {
//            return;
//        }
//        
//
//        String beanName = beanDescriptor.getFieldName();
//        String beanPath = PersistenceUtil.getPath(session, beanDescriptor, node);
//        
//        Object bean = null;
//        if (requestObjectCache.isCached(beanPath))
//        {
//            bean = requestObjectCache.getObject(beanPath);  
//            ReflectionUtils.setNestedProperty(object, beanName, bean);      
//        }
//        else 
//        {
//            Class beanClass = ReflectionUtils.getPropertyType(object, beanName);
//            
//            
//            String converterClassName = null;       
//            if (null == beanDescriptor.getConverter() || "".equals(beanDescriptor.getConverter())) 
//            {
//                converterClassName = DEFAULT_BEAN_CONVERTER;
//            }
//            else
//            {
//                converterClassName = beanDescriptor.getConverter();
//            }
//                        
//            Object[] param = {this.mapper, this, this.atomicTypeConverterProvider};         
//            BeanConverter beanConverter = (BeanConverter) ReflectionUtils.invokeConstructor(converterClassName, param);
//            if (beanDescriptor.isProxy()) 
//            {
//                bean = proxyManager.createBeanProxy(session, this, beanClass, beanConverter.getPath(session, beanDescriptor, node));
//            } 
//            else
//            {
//                bean = beanConverter.getObject(session, node, beanDescriptor,  mapper.getClassDescriptorByClass(beanClass), beanClass, bean);
//            }           
//            requestObjectCache.cache(beanPath, bean);           
//            ReflectionUtils.setNestedProperty(object, beanName, bean);
//        }
//    }
//    
//    
//}
