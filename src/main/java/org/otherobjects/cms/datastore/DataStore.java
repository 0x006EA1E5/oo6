package org.otherobjects.cms.datastore;

import java.util.List;

import org.otherobjects.cms.types.TypeDef;

/**
 * 
 * Where this is to be used:
 * 
 * WorkbenchController
 * FormController
 * BindService
 * 
 */
public interface DataStore
{
    List<Object> list(String query);

    Object create(TypeDef typeDef);

    Object get(String id);

    Object save(Object object);

    Object delete(Object object);
}
