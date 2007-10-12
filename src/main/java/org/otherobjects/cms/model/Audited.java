package org.otherobjects.cms.model;

import java.util.Date;

public interface Audited {
	public String getUserName();
	public void setUserName(String userName);
	public String getUserId();
	public void setUserId(String userId);
	public Date getModificationTimestamp();
	public void setModificationTimestamp(Date modificationTimestamp);
	public String getComment();
	public void setComment(String comment);
	public int getChangeNumber();
	public void setChangeNumber(int changeNumber);
}
