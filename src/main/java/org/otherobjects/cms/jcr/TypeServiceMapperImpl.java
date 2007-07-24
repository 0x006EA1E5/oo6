/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.otherobjects.cms.jcr;

import java.io.IOException;

import org.apache.jackrabbit.ocm.exception.IncorrectPersistentClassException;
import org.apache.jackrabbit.ocm.exception.InitMapperException;
import org.apache.jackrabbit.ocm.manager.beanconverter.impl.DefaultBeanConverterImpl;
import org.apache.jackrabbit.ocm.manager.beanconverter.impl.ReferenceBeanConverterImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.model.BeanDescriptor;
import org.apache.jackrabbit.ocm.mapper.model.ClassDescriptor;
import org.apache.jackrabbit.ocm.mapper.model.FieldDescriptor;
import org.apache.jackrabbit.ocm.mapper.model.MappingDescriptor;
import org.otherobjects.cms.types.JcrTypeServiceImpl;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class TypeServiceMapperImpl implements Mapper, InitializingBean
{
    private TypeService typeService;
    private MappingDescriptor mappingDescriptor;
    private Mapper staticMapper;

    /**
     * No-arg constructor.
     */
    public TypeServiceMapperImpl()
    {
    }

    public TypeServiceMapperImpl(Resource resource) throws IOException
    {
        this.staticMapper = new ResourceDigesterMappingImpl(resource);
    }

    protected Mapper buildMapper()
    {
        if (typeService != null)
        {
            this.mappingDescriptor = new MappingDescriptor();

            for (TypeDef typeDef : typeService.getTypes())
            {
                ClassDescriptor classDescriptor = createClassDescriptor(typeDef);
                mappingDescriptor.addClassDescriptor(classDescriptor);
            }

            this.mappingDescriptor.setMapper(this);
        }
        else
        {
            throw new InitMapperException("No mappings were provided");
        }

        return this;
    }

    protected ClassDescriptor createClassDescriptor(TypeDef typeDef)
    {
        ClassDescriptor cd = new ClassDescriptor();
        cd.setClassName(typeDef.getClassName());
        cd.setJcrNodeType("oo:node");

        // Add standard properties
        FieldDescriptor fd = new FieldDescriptor();
        fd.setFieldName("id");
        fd.setJcrName("id");
        fd.setUuid(true);
        cd.addFieldDescriptor(fd);

        FieldDescriptor fd2 = new FieldDescriptor();
        fd2.setFieldName("jcrPath");
        fd2.setJcrName("jcrPath");
        fd2.setPath(true);
        cd.addFieldDescriptor(fd2);

        FieldDescriptor fd3 = new FieldDescriptor();
        fd3.setFieldName("label");
        fd3.setJcrName("label");
        cd.addFieldDescriptor(fd3);

        FieldDescriptor fd4 = new FieldDescriptor();
        fd4.setFieldName("ooType");
        fd4.setJcrName("ooType");
        cd.addFieldDescriptor(fd4);

        // Add custom properties
        for (PropertyDef propDef : typeDef.getProperties())
        {
            String propertyType = propDef.getType();
            if (propertyType.equals("component"))
            {
                BeanDescriptor bd = new BeanDescriptor();
                bd.setFieldName("data." + propDef.getName());
                bd.setJcrName(propDef.getName());
                bd.setConverter(DefaultBeanConverterImpl.class.getName());
                cd.addBeanDescriptor(bd);
            }
            else if (propertyType.equals("reference"))
            {
                BeanDescriptor bd = new BeanDescriptor();
                bd.setFieldName("data." + propDef.getName());
                bd.setJcrName(propDef.getName());
                bd.setConverter(ReferenceBeanConverterImpl.class.getName());
                cd.addBeanDescriptor(bd);
            }
            else
            {
                FieldDescriptor f = new FieldDescriptor();
                f.setFieldName(propDef.getName());
                f.setJcrName(propDef.getName());
                f.setConverter(((JcrTypeServiceImpl) typeService).getJcrConverter(propertyType).getClass().getName());
                cd.addFieldDescriptor(f);
            }
        }
        return cd;
    }

    /**
    *
    * @see org.apache.jackrabbit.ocm.mapper.Mapper#getClassDescriptorByClass(java.lang.Class)
    */
    @SuppressWarnings("unchecked")
    public ClassDescriptor getClassDescriptorByClass(Class clazz)
    {
        // try static mappings first
        try
        {
            return staticMapper.getClassDescriptorByClass(clazz);
        }
        catch (RuntimeException e)
        {
        }

        // then dynamic mapping
        ClassDescriptor descriptor = mappingDescriptor.getClassDescriptorByName(clazz.getName());
        if (descriptor == null)
        {
            throw new IncorrectPersistentClassException("Class of type: " + clazz.getName() + " has no descriptor.");
        }
        return descriptor;
    }

    /**
    * @see org.apache.jackrabbit.ocm.mapper.Mapper#getClassDescriptorByNodeType(String)
    */
    public ClassDescriptor getClassDescriptorByNodeType(String jcrNodeType)
    {
        // try static mappings first
        ClassDescriptor descriptor = staticMapper.getClassDescriptorByNodeType(jcrNodeType);
        if (descriptor != null)
            return descriptor;

        // then dynamic mapping
        descriptor = mappingDescriptor.getClassDescriptorByNodeType(jcrNodeType);
        if (descriptor == null)
        {
            throw new IncorrectPersistentClassException("Node type: " + jcrNodeType + " has no descriptor.");
        }
        return descriptor;
    }

    public TypeService getTypeService()
    {
        return typeService;
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    public void afterPropertiesSet() throws Exception
    {
        Assert.isInstanceOf(JcrTypeServiceImpl.class, typeService);
        buildMapper();
    }
}
