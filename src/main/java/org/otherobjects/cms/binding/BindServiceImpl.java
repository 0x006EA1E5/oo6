package org.otherobjects.cms.binding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.beans.JcrBeanService;
import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * Very thin wrapper around springs ServletRequestDataBinder with our default CustomEditor(s) configured.
 * Call the bind method after you have beanified your DynaNode using {@link JcrBeanService} (This might change)
 * 
 * @author joerg
 *
 */
public class BindServiceImpl implements BindService
{
    private String dateFormat;
    private DynaNodeDao dynaNodeDao;
    
    static Pattern listPattern = Pattern.compile("^([\\S&&[^\\.]]*)\\[(\\d+)\\]"); // howevermany non-whitespace characters (apart from the dot) followed by at least one digit in square braces

    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)
    {
        this.dynaNodeDao = dynaNodeDao;
    }

    public void setDateFormat(String dateFormat)
    {
        this.dateFormat = dateFormat;
    }

    @SuppressWarnings("unchecked")
    public BindingResult bind(DynaNode dynaNode, HttpServletRequest request)
    {
        ServletRequestDataBinder binder = new ServletRequestDataBinder(dynaNode);

        // Create sub-objects where required
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String propertyName = parameterNames.nextElement();
//            if (propertyName.contains("."))
//            {
//                // Reference to sub object
//                createSubObjects(dynaNode, dynaNode.getTypeDef(), propertyName);
//            }
//            
//            Matcher matcher = listPattern.matcher(propertyName);
//            if(matcher.matches())
//            	createList(dynaNode, dynaNode.getTypeDef(), propertyName, matcher);
            
            if(propertyName.contains(".") || listPattern.matcher(propertyName).matches()) // this is a nested property or a list
            	createNextObject(dynaNode, dynaNode.getTypeDef(), propertyName);
            
        }

        // Add reference custom editors
        addReferenceEditors(dynaNode.getTypeDef(), binder, "");

        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(new SimpleDateFormat(dateFormat), true));
        binder.bind(request);

        return binder.getBindingResult();
    }
    
    /**
     * walks the propertyPath and creates components and lists of components along the way.
     * Stops once all of the propertyPath is consumed or if it comes across a simple property or 
     * a list that contains simple properties.
     * If the properties it finds along the way are already there it leaves them alone.
     * @param parent
     * @param typeDef
     * @param propertyPath
     */
    @SuppressWarnings("unchecked")
    protected void createNextObject(Object parent, TypeDef typeDef,
			String propertyPath) {
    	Matcher matcher = listPattern.matcher(propertyPath);
    	String propertyName;
    	String leftOverPath;
    	String listWithIndex = null;
    	int listIndex = -1;
    	boolean isList = false;
    	if(matcher.lookingAt()) // we have a list
    	{
    		isList = true;
    		propertyName = matcher.group(1);
    		listWithIndex = matcher.group();
    		listIndex = Integer.parseInt(matcher.group(2));
    		int thisPartPathLength = listWithIndex.length();
    		if(thisPartPathLength < propertyPath.length())
    			leftOverPath = propertyPath.substring(thisPartPathLength + 1);
    		else
    			leftOverPath = null;
    	}
    	else // subObject other than list
    	{
    		int dotIndex = propertyPath.indexOf(".");
    		if(dotIndex > -1)
    		{
    			propertyName = propertyPath.substring(0, dotIndex);
    			//Assert.isTrue(dotIndex + 1 < propertyName.length(), "a valid propertyPath can't end in a dot");
    			leftOverPath = propertyPath.substring(dotIndex + 1);
    		}
    		else
    		{
    			propertyName = propertyPath; // apparently we reached the end leaf
    			leftOverPath = null;
    		}
    	}
    	
    	// find out type of object
    	Object newParent = null;
    	PropertyDef propertyDef = typeDef.getProperty(propertyName);
    	// TODO Show complete paramater name at this point
    	Assert.notNull(propertyDef, "No property found for parameter path: " + propertyName);
    	TypeDef newTypeDef = null;
    	if(isList)
    	{
    		List listProperty;
    		// this is a list so it needs to have a collectionElementType in propertyDef
    		String collectionElementType = propertyDef.getCollectionElementType();
        	Assert.notNull("If this property is a collection the collectionElementType needs to have been set: " + propertyDef.getName());
        	
    		// create list
    		try {
    			listProperty = (List) PropertyUtils.getSimpleProperty(parent, propertyName);
				if(listProperty == null)
				{
					listProperty = new ArrayList();
					PropertyUtils.setSimpleProperty(parent, propertyName, listProperty);
				}

				fillList(listProperty, listIndex + 1);
				
				if(collectionElementType.equals(PropertyDef.COMPONENT)) // we only create list elements that are supposed to be components
				{
					if(listProperty.get(listIndex) == null) 
					{
						newTypeDef = propertyDef.getRelatedTypeDef();
						newParent = getRelatedTypeInstance(newTypeDef);
						listProperty.set(listIndex, newParent);
					}
					else
					{
						newTypeDef = propertyDef.getRelatedTypeDef();
						newParent = listProperty.get(listIndex);
					}
				}
			} catch (Exception e) {
				throw new OtherObjectsException("Error getting or instantiating list property: " + propertyName, e);
			}
		}
    	else if(propertyDef.getType().equals(PropertyDef.COMPONENT))
    	{
    		try {
    			newTypeDef = propertyDef.getRelatedTypeDef();
    			newParent = PropertyUtils.getSimpleProperty(parent, propertyName);
    			if(newParent == null)
    			{
    				newParent = getRelatedTypeInstance(newTypeDef);
					PropertyUtils.setSimpleProperty(parent, propertyName, newParent);
    			}
			} catch (Exception e) {
				throw new OtherObjectsException("Error getting or instantiating component property: " + propertyName, e);
			} 
    	}
    	else
    	{
    		// neither a list nor a component - stop here
    		return;
    	}
    	
    	
    	if(leftOverPath != null && newParent != null && newTypeDef != null)
    		createNextObject(newParent, newTypeDef, leftOverPath);
    }

	private Object getRelatedTypeInstance(TypeDef typeDef) throws Exception {
		
		Object relatedTypeInstance = Class.forName(typeDef.getClassName()).newInstance();
		PropertyUtils.setSimpleProperty(relatedTypeInstance, "ooType", typeDef.getName());
		return relatedTypeInstance;
	}

	private void addReferenceEditors(TypeDef typeDef, ServletRequestDataBinder binder, String prefix)
    {
        for (PropertyDef pd : typeDef.getProperties())
        {
            if (pd.getType().equals(PropertyDef.REFERENCE))
            {
                binder.registerCustomEditor(DynaNode.class, prefix + pd.getName(), new DynaNodeReferenceEditor(dynaNodeDao));
            }
            else if (pd.getType().equals(PropertyDef.COMPONENT))
            {
                // Cascade through component looking for references
                addReferenceEditors(pd.getRelatedTypeDef(), binder, prefix + pd.getName() + ".");
            }
        }
    }
    
    private void fillList(List<?> list, int size) {
    	int initialSize = list.size();
    	if(initialSize >= size)
    		return;
    	
    	for(int i = 0; i < (size - initialSize); i++)
    	{
    		list.add(null);
    	}
	}

}
