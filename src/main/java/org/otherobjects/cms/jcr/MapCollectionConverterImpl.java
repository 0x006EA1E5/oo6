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

import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;

import org.apache.jackrabbit.ocm.exception.PersistenceException;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.model.CollectionDescriptor;
import org.apache.jackrabbit.ocm.persistence.atomictypeconverter.AtomicTypeConverter;
import org.apache.jackrabbit.ocm.persistence.collectionconverter.ManageableCollection;
import org.apache.jackrabbit.ocm.persistence.collectionconverter.impl.AbstractCollectionConverterImpl;
import org.apache.jackrabbit.ocm.persistence.collectionconverter.impl.ManagedHashMap;
import org.apache.jackrabbit.ocm.persistence.objectconverter.ObjectConverter;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;

@SuppressWarnings("unchecked")
public class MapCollectionConverterImpl extends AbstractCollectionConverterImpl
{

    /**
     * Constructor
     *
     * @param atomicTypeConverters
     * @param objectConverter
     * @param mapper
     */
    public MapCollectionConverterImpl(Map atomicTypeConverters, ObjectConverter objectConverter, Mapper mapper)
    {
        super(atomicTypeConverters, objectConverter, mapper);
    }

    /**
     *
     * @see AbstractCollectionConverterImpl#doInsertCollection(Session, Node, CollectionDescriptor, ManageableCollection)
     */
    protected void doInsertCollection(Session session, Node parentNode, CollectionDescriptor collectionDescriptor, ManageableCollection collection) throws RepositoryException
    {
        try
        {
            if (collection == null)
            {
                return;
            }

            String jcrName = getCollectionJcrName(collectionDescriptor);
            Node dataNode = parentNode.addNode(jcrName, "nt:unstructured");
            ValueFactory valueFactory = session.getValueFactory();
            Map map = (Map) collection;

            String ooType = parentNode.getProperty("ooType").getString();
            TypeService ts = TypeService.getInstance();
            TypeDef type = ts.getType(ooType);

            Iterator i = map.keySet().iterator();
            while (i.hasNext())
            {
                String key = (String) i.next();
                Object fieldValue = map.get(key);
                AtomicTypeConverter atomicTypeConverter = (AtomicTypeConverter) ts.getJcrConverter(type.getProperty(key).getType());
                Value value = atomicTypeConverter.getValue(valueFactory, fieldValue);
                dataNode.setProperty(key, value);
            }

        }
        catch (ValueFormatException vfe)
        {
            throw new PersistenceException("Cannot insert collection field : " + collectionDescriptor.getFieldName() + " of class " + collectionDescriptor.getClassDescriptor().getClassName(), vfe);
        }
    }

    /**
     *
     * @see AbstractCollectionConverterImpl#doUpdateCollection(Session, Node, CollectionDescriptor, ManageableCollection)
     */
    protected void doUpdateCollection(Session session, Node parentNode, CollectionDescriptor collectionDescriptor, ManageableCollection collection) throws RepositoryException
    {
        String jcrName = getCollectionJcrName(collectionDescriptor);

        // Delete existing values
        if (parentNode.hasNode(jcrName))
        {
            parentNode.getNode(jcrName).remove();
        }

        // Add values
        doInsertCollection(session, parentNode, collectionDescriptor, collection);
    }

    /**
     * @see AbstractCollectionConverterImpl#doGetCollection(Session, Node, CollectionDescriptor, Class)
     */
    protected ManageableCollection doGetCollection(Session session, Node parentNode, CollectionDescriptor collectionDescriptor, Class collectionFieldClass) throws RepositoryException
    {
        try
        {
            String jcrName = getCollectionJcrName(collectionDescriptor);
            if (!parentNode.hasNode(jcrName))
            {
                return null;
            }

            String ooType = parentNode.getProperty("ooType").getString();
            TypeService ts = TypeService.getInstance();
            TypeDef type = ts.getType(ooType);

            Node dataNode = parentNode.getNode(jcrName);

            ManagedHashMap map = new ManagedHashMap();

            PropertyIterator properties = dataNode.getProperties();
            while (properties.hasNext())
            {
                Property p = properties.nextProperty();
                PropertyDef pd = type.getProperty(p.getName());
                if (pd != null)
                {
                    // Ignore non-defined properties
                    AtomicTypeConverter atomicTypeConverter = (AtomicTypeConverter) ts.getJcrConverter(pd.getType());
                    map.put(p.getName(), atomicTypeConverter.getObject(p.getValue()));
                }
            }

            return map;
        }
        catch (ValueFormatException vfe)
        {
            throw new PersistenceException("Cannot get the collection field : " + collectionDescriptor.getFieldName() + "for class " + collectionDescriptor.getClassDescriptor().getClassName(), vfe);
        }
    }

    /**
     * @see AbstractCollectionConverterImpl#doIsNull(Session, Node, CollectionDescriptor, Class)
     */
    protected boolean doIsNull(Session session, Node parentNode, CollectionDescriptor collectionDescriptor, Class collectionFieldClass) throws RepositoryException
    {
        String jcrName = getCollectionJcrName(collectionDescriptor);

        if (!parentNode.hasNode(jcrName))
        {
            return true;
        }
        return false;
    }
}