package org.otherobjects.cms.binding;

import java.lang.reflect.InvocationTargetException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(BindServiceImpl.class);

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
     * walks the propertyPaths and creates components and lists of components along the way.
     * Stops once all of the propertyPath is consumed or if comes across a simple property or 
     * a list that contains simple properties.
     * @param parent
     * @param typeDef
     * @param propertyPath
     */
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
    	TypeDef newTypeDef = null;
    	if(isList)
    	{
    		List listProperty;
    		// create list
    		try {
    			listProperty = (List) PropertyUtils.getSimpleProperty(parent, propertyName);
				if(listProperty == null)
				{
					listProperty = new ArrayList();
					PropertyUtils.setSimpleProperty(parent, propertyName, listProperty);
				}

				fillList(listProperty, listIndex + 1);
				
				if(propertyDef.getType().equals(PropertyDef.COMPONENT) && listProperty.get(listIndex) == null) // we only create list elements that are supposed to be components
				{
					newTypeDef = propertyDef.getRelatedTypeDef();
					newParent = getRelatedTypeInstance(newTypeDef);
					listProperty.set(listIndex, newParent);
				}
			} catch (Exception e) {
				throw new OtherObjectsException("Error instantiating list property: " + propertyName, e);
			}
		}
    	else if(propertyDef.getType().equals(PropertyDef.COMPONENT))
    	{
    		try {
    			newTypeDef = propertyDef.getRelatedTypeDef();
    			newParent = getRelatedTypeInstance(newTypeDef);
				PropertyUtils.setSimpleProperty(parent, propertyName, newParent);
			} catch (Exception e) {
				throw new OtherObjectsException("Error instantiating component property: " + propertyName, e);
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
    
    
//    protected void createList(DynaNode dynaNode, TypeDef typeDef, String propertyPath, Matcher listMatcher)
//    {
//        if(listMatcher.matches()) // this seems to be a list - create it if it doesn't exist
//        {
//        	String listSubPath = listMatcher.group(0); // something like: nameOfList[1]
//        	String listName = listMatcher.group(1); // just the name: nameOfList
//        	
//        	// now we need to correct the propertyPath to read something like 
//        	// beanProperty.listProperty instead of beanProperty.listProperty[1]
//        	// so we cut off the last occurence of listSubPath and add listName instead
//        	
//        	int thisListIndex = propertyPath.lastIndexOf(listSubPath);
//        	Assert.isTrue(thisListIndex > -1, "This list needs to be in the propertyPath");
//        	
//        	propertyPath = propertyPath.substring(0, thisListIndex) + listName;
//        	
//        	int listIndex = -1; 
//        	try{
//        		listIndex = Integer.parseInt(listMatcher.group(2)); // just the index
//        	}
//        	catch(NumberFormatException e)
//        	{
//        		throw new OtherObjectsException("Error parsing list index: " + propertyPath, e);
//        	}
//        	
//        	List listProperty = null;
//        	
//        	
//        	try {
//        		listProperty = (List) PropertyUtils.getNestedProperty(dynaNode, propertyPath);
//				if(listProperty == null)
//				{
//					listProperty = new ArrayList();
//					PropertyUtils.setNestedProperty(dynaNode, propertyPath, listProperty);
//				}
//				
//			} catch (Exception e) {
//				throw new OtherObjectsException("Error instantiating list property: " + listName, e);
//			}
//			
//			// set indexed property if not yet set
//			Assert.isTrue(listIndex > -1, "This should have been parsed to something greater than -1, otherwise some parsing ex would have happenend and we wouldn't be here");
//			fillList(listProperty, listIndex + 1);
//			if(listProperty.get(listIndex) == null)
//			{
//				// type of property in list
//				String listElementType = dynaNode.getTypeDef().getProperty(propertyPath).getType();
//				if(listElementType.equals(PropertyDef.COMPONENT)) // only do something for components, not for simple properties
//				{
//					TypeDef relatedTypeDef = dynaNode.getTypeDef().getProperty(listName).getRelatedTypeDef();
//					try {
//						DynaNode subObject = (DynaNode) Class.forName(relatedTypeDef.getClassName()).newInstance();
//						subObject.setOoType(relatedTypeDef.getName());
//		                listProperty.set(listIndex, subObject);
//					} catch (Exception e) {
//						throw new OtherObjectsException("Couldn't create/set indexed component", e);
//					}	
//	            }
//			}
//        		
//        }
//    }
//    
//    /**
//     * Creates necessary sub objects. These are required for storing
//     * component and list properties but not for references. TODO Explain better.
//     * 
//     * @param dynaNode
//     * @param propertyPath
//     */
//    protected void createSubObjects(DynaNode dynaNode, TypeDef typeDef, String propertyPath)
//    {
//        //FIXME Can we do with reflection rather than TypeService?
//        int index = 0;
//        int start = 0;
//        while (true)
//        {
//            index = propertyPath.indexOf(".", index + 1);
//            if (index < 0)
//                break;
//            String partPath = propertyPath.substring(0, index);
//            String propertyName = propertyPath.substring(start, index);
//            start = index + 1;
//            Matcher listMatcher = listPattern.matcher(propertyName);
//            if(listMatcher.matches())
//            {
//            	createList(dynaNode, typeDef, propertyPath, listMatcher);
//            	continue;
//            }
//            
//            PropertyDef property = dynaNode.getTypeDef().getProperty(partPath);
//	
//            //TODO More intelligent checking here to avoid repeat calls
//            createSubObject(dynaNode, partPath, property.getRelatedTypeDef());
//        }
//    }
//
    private void fillList(List list, int size) {
    	int initialSize = list.size();
    	if(initialSize >= size)
    		return;
    	
    	for(int i = 0; i < (size - initialSize); i++)
    	{
    		list.add(null);
    	}
	}
//
//	protected void createSubObject(DynaNode dynaNode, String propertyName, TypeDef relatedTypeDef)
//    {
//        if (dynaNode.get(propertyName) == null)
//        {
//            logger.info("Creating sub object {} at {}.", relatedTypeDef.getName(), propertyName);
//            try
//            {
//                DynaNode subObject = (DynaNode) Class.forName(relatedTypeDef.getClassName()).newInstance();
//                subObject.setOoType(relatedTypeDef.getName());
//                dynaNode.set(propertyName, subObject);
//            }
//            catch (Exception e)
//            {
//                throw new OtherObjectsException("Error creating subObject: " + propertyName, e);
//            }
//        }
//    }
//
}
