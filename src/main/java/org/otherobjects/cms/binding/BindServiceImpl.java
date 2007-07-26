package org.otherobjects.cms.binding;

import java.text.SimpleDateFormat;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.beans.JcrBeanService;
import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
            PropertyDef property = dynaNode.getTypeDef().getProperty(p);

            //TODO More intelligent checking here to avoid repeat calls
            createSubObject(dynaNode, p, property.getRelatedTypeDef());
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
