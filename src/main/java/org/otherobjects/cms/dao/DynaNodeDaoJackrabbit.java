package org.otherobjects.cms.dao;

import java.util.Date;
import java.util.List;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.jcr.GenericJcrDaoJackrabbit;
import org.otherobjects.cms.jcr.OtherObjectsJackrabbitSessionFactory;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

public class DynaNodeDaoJackrabbit extends GenericJcrDaoJackrabbit<DynaNode> implements DynaNodeDao
{
    private TypeService typeService;
    private OtherObjectsJackrabbitSessionFactory sessionFactory;
    
    
//    private DynaNodeValidator dynaNodeValidator;

    public void setSessionFactory(
			OtherObjectsJackrabbitSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public DynaNodeDaoJackrabbit()
    {
        super(DynaNode.class);
    }

    public DynaNode create(String typeName)
    {
        Assert.notNull(typeName, "typeName must not be null.");
        TypeDef type = typeService.getType(typeName);
        try
        {
            DynaNode n = (DynaNode) Class.forName(type.getClassName()).newInstance();
            n.setTypeDef(type);
            return n;
        }
        catch (Exception e)
        {
            //TODO Better exception?
            throw new OtherObjectsException("Could not create new instance of type: " + typeName, e);
        }
    }

    public TypeService getTypeService()
    {
        return typeService;
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    @Override
    public DynaNode save(DynaNode object, boolean validate)
    {
        if (validate)
        {
            Errors errors = new BeanPropertyBindingResult(object, "target");
            // FIXME M2 Re-enable validation after dynaNodes are true beans
            //dynaNodeValidator.validate(object, errors);
            if (errors.getErrorCount() > 0)
                throw new OtherObjectsException("DynaNode " + object.getLabel() + "couldn't be validated and therefore didn't get saved");
        }
        updateAuditInfo(object, "Saved");
        object.setPublished(false); // on saving the publish state gets set to false
        return super.save(object, false);
    }
    
    private DynaNode saveInternal(DynaNode dynaNode, boolean publishStatus)
    {
    	dynaNode.setPublished(publishStatus);
    	return super.save(dynaNode, false); 
    }

    @SuppressWarnings("unchecked")
    public List<DynaNode> getAllByType(String typeName)
    {
        try
        {
            TypeDef type = typeService.getType(typeName);
            QueryManager queryManager = getJcrMappingTemplate().createQueryManager();
            Filter filter = queryManager.createFilter(Class.forName(type.getClassName()));
            Query query = queryManager.createQuery(filter);
            //filter.setScope(path + "/");
            filter.addEqualTo("ooType", typeName);
            return (List<DynaNode>) getJcrMappingTemplate().getObjects(query);
        }
        catch (ClassNotFoundException e)
        {
            throw new OtherObjectsException("No class found for type: " + typeName);
        }
        
//        return (List<DynaNode>) getJcrMappingTemplate().execute(new JcrMappingCallback()
//        {
//            public Object doInJcrMapping(ObjectContentManager manager) throws JcrMappingException
//            {
//                try
//                {
//                    
//                }
//                catch (Exception e)
//                {
//                    throw new JcrMappingException(e);
//                }
//            }
//        }, true);
    }

//    public void setDynaNodeValidator(DynaNodeValidator dynaNodeValidator)
//    {
//        this.dynaNodeValidator = dynaNodeValidator;
//    }
    
    public void publish(DynaNode dynaNode)
    {
    	//FIXME this should display proper transactional behaviour which it doesn't at the moment as there are multiple jcr sessions involved
    	if(dynaNode.isPublished())
    		throw new OtherObjectsException("DynaNode " + dynaNode.getJcrPath() + "[" + dynaNode.getId() + "] couldn't be published as its published flag is already set ");
    	
    	Session liveSession = null;
    	Session editSession = null;
    	try {
    		// get a live workspace session
			liveSession = sessionFactory.getSession(OtherObjectsJackrabbitSessionFactory.LIVE_WORKSPACE_NAME);
			Workspace liveWorkspace = liveSession.getWorkspace();
			
			Node liveNode = null;
			try{
				//get the corresponding node in the live workspace by UUID in case path has changed
				liveNode = liveSession.getNodeByUUID(dynaNode.getId());
			}catch(ItemNotFoundException e)	{} // noop
			
			if( liveNode == null) // no such node so we can clone
			{
				liveWorkspace.clone(OtherObjectsJackrabbitSessionFactory.EDIT_WORKSPACE_NAME, dynaNode.getJcrPath(), dynaNode.getJcrPath(), true);
			}
			else
			{
				liveNode.update(OtherObjectsJackrabbitSessionFactory.EDIT_WORKSPACE_NAME);
			}
			
			// we got here so we successfully published
			updateAuditInfo(dynaNode, "Published");
			saveInternal(dynaNode, true); // set the status to published
			editSession = sessionFactory.getSession(OtherObjectsJackrabbitSessionFactory.EDIT_WORKSPACE_NAME);
			Node editNode = editSession.getNodeByUUID(dynaNode.getId());
			// create version
			editNode.checkin();
			editNode.checkout();
			
		} catch (RepositoryException e) {
			throw new OtherObjectsException("Couldn't publish DynaNode with path " + dynaNode.getJcrPath(), e);
		}
		finally
		{
			if(liveSession != null)
				liveSession.logout();
			if(editSession != null)
				editSession.logout();
		}
    }
    
    private void updateAuditInfo(DynaNode dynaNode, String comment)
    {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Assert.isTrue(principal instanceof User, "Can't do auditing if principal isn't your user class");
    	User user = (User) principal;
    	dynaNode.setUserName(user.getFullName());
    	dynaNode.setUserId(user.getId().toString());
    	dynaNode.setModificationTimestamp(new Date());
    	dynaNode.setComment(comment);
    	dynaNode.setChangeNumber(dynaNode.getChangeNumber() + 1);
    }
    
    

}
