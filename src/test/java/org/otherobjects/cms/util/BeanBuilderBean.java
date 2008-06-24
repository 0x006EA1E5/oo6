/*
 * Created on May 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.otherobjects.cms.util;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author michaelh
 */
@SuppressWarnings("unchecked")
public class BeanBuilderBean
{

    private Map mapInstance;
    private HashMap hashMapInstance;
    private List listInstance;
    private ArrayList arrayListInstance;
    private Set setInstance;
    private TreeSet treeSetInstance;
    private Collection collectionInstance;

    private Object objectInstance;
    private BeanBuilderBean beanInstance;

    private Object[] objectArray;
    private BeanBuilderBean[] beanArray;

    private String stringInstance;

    private ArrayList beanList = new ArrayList();

    public Map getMapInstance()
    {
        return mapInstance;
    }

    public void setMapInstance(Map value)
    {
        mapInstance = value;
    }

    public HashMap getHashMapInstance()
    {
        return hashMapInstance;
    }

    public void setHashMapInstance(HashMap value)
    {
        hashMapInstance = value;
    }

    public List getListInstance()
    {
        return listInstance;
    }

    public void setListInstance(List value)
    {
        listInstance = value;
    }

    public ArrayList getArrayListInstance()
    {
        return arrayListInstance;
    }

    public void setArrayListInstance(ArrayList value)
    {
        arrayListInstance = value;
    }

    public Set getSetInstance()
    {
        return setInstance;
    }

    public void setSetInstance(Set value)
    {
        setInstance = value;
    }

    public TreeSet getTreeSetInstance()
    {
        return treeSetInstance;
    }

    public void setTreeSetInstance(TreeSet value)
    {
        treeSetInstance = value;
    }

    public Collection getCollectionInstance()
    {
        return collectionInstance;
    }

    public void setCollectionInstance(Collection value)
    {
        collectionInstance = value;
    }

    public Object getObjectInstance()
    {
        return objectInstance;
    }

    public void setObjectInstance(Object value)
    {
        objectInstance = value;
    }

    public BeanBuilderBean getBeanInstance()
    {
        return beanInstance;
    }

    public void setBeanInstance(BeanBuilderBean value)
    {
        beanInstance = value;
    }

    public Object[] getObjectArray()
    {
        return objectArray;
    }

    public void setObjectArray(Object[] value)
    {
        objectArray = value;
    }

    public BeanBuilderBean[] getBeanArray()
    {
        return beanArray;
    }

    public void setBeanArray(BeanBuilderBean[] value)
    {
        beanArray = value;
    }

    public String getStringInstance()
    {
        return stringInstance;
    }

    public void setStringInstance(String value)
    {
        stringInstance = value;
    }

    public List getBeanList()
    {
        return beanList;
    }

    public void addBean(BeanBuilderBean value)
    {
        beanList.add(value);
    }

}
