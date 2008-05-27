package org.otherobjects.cms.model;

import java.util.Date;

public interface Audited
{
    String getUserName();

    void setUserName(String userName);

    String getUserId();

    void setUserId(String userId);

    Date getModificationTimestamp();

    void setModificationTimestamp(Date modificationTimestamp);

    String getComment();

    void setComment(String comment);

    int getChangeNumber();

    void setChangeNumber(int changeNumber);
}
