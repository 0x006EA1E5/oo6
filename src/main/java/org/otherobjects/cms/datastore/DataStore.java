package org.otherobjects.cms.datastore;

import java.util.List;

import org.otherobjects.cms.dao.GenericDao;
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
    @SuppressWarnings("unchecked")
    GenericDao getDao(TypeDef typeDef);

    List<Object> list(String query);

    Object create(TypeDef typeDef, String containerId);

    Object get(String id);

    Object save(Object object);

    Object delete(Object object);
}
