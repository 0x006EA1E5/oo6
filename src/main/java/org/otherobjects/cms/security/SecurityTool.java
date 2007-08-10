package org.otherobjects.cms.security;

import org.acegisecurity.context.SecurityContextHolder;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.model.User;

/**
 * A tool to provide static convenience methods to work with Spring security's {@link SecurityContextHolder}
 * 
 * @author joerg
 *
 */
public class SecurityTool {
	
	/**
	 * @return The id of the current user or null if no user is associated with the current thread.
	 */
	public static Long getUserId()
	{
		User currentUser = getCurrentUser();
		if(currentUser == null)
			return null;
		else
			return currentUser.getId();
	}
	
	/**
	 * @param id
	 * @return True if the passed in id is equal to the id of the current User.  Else false.
	 */
	public static boolean isCurrentUser(String id)
	{
		Long currentUserId = getUserId();
		if(id == null || currentUserId == null)
			return false;
		
		return id.equals(currentUserId.toString());
	}
	
	/**
	 * @return Current user or null if no user associated with current thread.
	 */
	public static User getCurrentUser()
	{
		if(SecurityContextHolder.getContext().getAuthentication() != null)
		{
			try{
				User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				return user;
			}
			catch(ClassCastException e)
			{
				throw new OtherObjectsException("Current principal is not of type org.otherobjects.cms.model.User", e);
			}
		}
		else
			return null;
	}
}
