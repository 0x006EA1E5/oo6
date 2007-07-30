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
    
    static Pattern listPattern = Pattern.compile("^(\\S*)\\[(\\d+)\\]$");

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
            if (propertyName.contains("."))
            {
                // Reference to sub object
                createSubObjects(dynaNode, propertyName);
            }
            
            Matcher matcher = listPattern.matcher(propertyName);
            if(matcher.matches())
            	createList(dynaNode, propertyName, matcher);
            
        }

        // Add reference custom editors
        addReferenceEditors(dynaNode.getTypeDef(), binder, "");

        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(new SimpleDateFormat(dateFormat), true));
        binder.bind(request);

        return binder.getBindingResult();
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
    
    
    protected void createList(DynaNode dynaNode, String propertyName, Matcher listMatcher)
    {
        if(listMatcher.matches()) // this seems to be a list - create it if it doesn't exist
        {
        	String listName = listMatcher.group(1);
        	int listIndex = -1;
        	try{
        		listIndex = Integer.parseInt(listMatcher.group(2));
        	}
        	catch(NumberFormatException e)
        	{
        		throw new OtherObjectsException("Error parsing list index: " + propertyName, e);
        	}
        	
        	List listProperty = null;
        	
        	
        	try {
        		listProperty = (List) PropertyUtils.getSimpleProperty(dynaNode, listName);
				if(listProperty == null)
				{
					listProperty = new ArrayList();
					PropertyUtils.setSimpleProperty(dynaNode, listName, listProperty);
				}
				
			} catch (Exception e) {
				throw new OtherObjectsException("Error instantiating list property: " + listName, e);
			}
			
			// set indexed property if not yet set
			Assert.isTrue(listIndex > -1, "This should have been parsed to something greater than -1, otherwise some parsing ex would have happenend and we wouldn't be here");
			fillList(listProperty, listIndex + 1);
			if(listProperty.get(listIndex) == null)
			{
				// type of property in list
				String listElementType = dynaNode.getTypeDef().getProperty(listName).getType();
				if(listElementType.equals(PropertyDef.COMPONENT)) // only do something for components, not for simple properties
				{
					TypeDef relatedTypeDef = dynaNode.getTypeDef().getProperty(listName).getRelatedTypeDef();
					try {
						DynaNode subObject = (DynaNode) Class.forName(relatedTypeDef.getClassName()).newInstance();
						subObject.setOoType(relatedTypeDef.getName());
		                listProperty.set(listIndex, subObject);
					} catch (Exception e) {
						throw new OtherObjectsException("Couldn't create/set indexed component", e);
					}	
	            }
			}
        		
        }
    }
    
    /**
     * Creates necessary sub objects. These are required for storing
     * component and list properties but not for references. TODO Explain better.
     * 
     * @param dynaNode
     * @param propertyName
     */
    protected void createSubObjects(DynaNode dynaNode, String propertyName)
    {
        //FIXME Can we do with reflection rather than TypeService?
        int index = 0;
        while (true)
        {
            index = propertyName.indexOf(".", index + 1);
            if (index < 0)
                break;
            String p = propertyName.substring(0, index);
            Matcher listMatcher = listPattern.matcher(p);
            if(listMatcher.matches())
            {
            	createList(dynaNode, propertyName, listMatcher);
            	continue;
            }
            
            PropertyDef property = dynaNode.getTypeDef().getProperty(p);
	
            //TODO More intelligent checking here to avoid repeat calls
            createSubObject(dynaNode, p, property.getRelatedTypeDef());
        }
    }

    private void fillList(List listProperty, int size) {
		while(listProperty.size() < size)
		{
			listProperty.add(null);
		}
		
	}

	protected void createSubObject(DynaNode dynaNode, String propertyName, TypeDef relatedTypeDef)
    {
        if (dynaNode.get(propertyName) == null)
        {
            logger.info("Creating sub object {} at {}.", relatedTypeDef.getName(), propertyName);
            try
            {
                DynaNode subObject = (DynaNode) Class.forName(relatedTypeDef.getClassName()).newInstance();
                subObject.setOoType(relatedTypeDef.getName());
                dynaNode.set(propertyName, subObject);
            }
            catch (Exception e)
            {
                throw new OtherObjectsException("Error creating subObject: " + propertyName, e);
            }
        }
    }

}
