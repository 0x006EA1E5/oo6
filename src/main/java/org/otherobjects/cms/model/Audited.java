package org.otherobjects.cms.model;

import java.util.Date;

public interface Audited
{
    String getCreator();

    void setCreator(String creator);

    Date getCreationTimestamp();

    void setCreationTimestamp(Date creationTimestamp);

    String getModifier();

    void setModifier(String modifier);

    Date getModificationTimestamp();

    void setModificationTimestamp(Date modificationTimestamp);

    String getEditingComment();

    void setEditingComment(String comment);

    int getVersion();

    void setVersion(int version);
}
