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
package org.apache.jackrabbit.ocm.spring;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.ocm.exception.JcrMappingException;
import org.apache.jackrabbit.ocm.persistence.PersistenceManager;


/**
 * Template whichs adds mapping support for the Java Content Repository.
 * <p/>
 * For PersistenceManagers the template creates internally the set of default converters.
 * 
 * 
 * @see org.apache.portals.graffito.jcr.persistence.PersistenceManager
 * @author Costin Leau
 * 
 */
@SuppressWarnings("unchecked")
public class JcrTypedMappingTemplate extends JcrMappingTemplate implements JcrMappingOperations
{
    @Override
    protected PersistenceManager createPersistenceManager(Session session) throws RepositoryException, JcrMappingException
    {
        return super.createPersistenceManager(session);
    }
}