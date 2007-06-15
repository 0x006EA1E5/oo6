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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.ocm.exception.IncorrectPersistentClassException;
import org.apache.jackrabbit.ocm.exception.InitMapperException;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.model.ClassDescriptor;
import org.apache.jackrabbit.ocm.mapper.model.FieldDescriptor;
import org.apache.jackrabbit.ocm.mapper.model.MappingDescriptor;
import org.apache.jackrabbit.ocm.persistence.atomictypeconverter.impl.StringTypeConverterImpl;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;

public class TypeServiceMapperImpl implements Mapper
{
    private static final Log log = LogFactory.getLog(TypeServiceMapperImpl.class);

    private MappingDescriptor mappingDescriptor;
    private Collection rootClassDescriptors = new ArrayList(); // contains the class descriptor which have not ancestors 

    /**
     * No-arg constructor.
     */
    public TypeServiceMapperImpl()
    {
    }

    public TypeServiceMapperImpl(TypeService typeService)
    {
        this.buildMapper(typeService);
    }

    protected Mapper buildMapper(TypeService typeService)
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
        cd.setClassName(CmsNode.class.getName());
        cd.setJcrNodeType("nt:unstructured");
        
        // Add standard properties
        FieldDescriptor fd = new FieldDescriptor();
        fd.setFieldName("id");
        fd.setJcrName("id");
        fd.setUuid(false);
        cd.addFieldDescriptor(fd);

        FieldDescriptor fd2 = new FieldDescriptor();
        fd2.setFieldName("path");
        fd2.setJcrName("path");
        fd2.setPath(true);
        cd.addFieldDescriptor(fd2);
        
        FieldDescriptor fd3 = new FieldDescriptor();
        fd3.setFieldName("label");
        fd3.setJcrName("label");
        cd.addFieldDescriptor(fd3);
        
        FieldDescriptor fd4 = new FieldDescriptor();
        fd4.setFieldName("description");
        fd4.setJcrName("description");
        cd.addFieldDescriptor(fd4);
        
        // Add custom properties
        for(PropertyDef propDef : typeDef.getProperties())
        {
            FieldDescriptor f = new FieldDescriptor();
            f.setFieldName("data." + propDef.getName());
            f.setJcrName(propDef.getName());
            f.setConverter(StringTypeConverterImpl.class.getName());
            cd.addFieldDescriptor(f);
        }
        return cd;
    }

    /**
    *
    * @see org.apache.jackrabbit.ocm.mapper.Mapper#getClassDescriptorByClass(java.lang.Class)
    */
    public ClassDescriptor getClassDescriptorByClass(Class clazz)
    {
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
        ClassDescriptor descriptor = mappingDescriptor.getClassDescriptorByNodeType(jcrNodeType);
        if (descriptor == null)
        {
            throw new IncorrectPersistentClassException("Node type: " + jcrNodeType + " has no descriptor.");
        }
        return descriptor;
    }

}
