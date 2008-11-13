package org.otherobjects.cms.datastore;

import java.util.List;

public interface DataStore
{
    List<Object> list(String query);

    Object get(String id);

    Object save(Object object);

    Object delete(Object object);
}
