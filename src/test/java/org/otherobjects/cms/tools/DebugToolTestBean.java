package org.otherobjects.cms.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DebugToolTestBean
{
    private Long id;
    private String firstName;
    private String lastName;
    private String description;
    private String[] emailAddresses;
    private List<DebugToolTestBean> childDummyBeans;
    private boolean active;

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String[] getEmailAddresses()
    {
        return emailAddresses;
    }

    public void setEmailAddresses(String[] emailAddresses)
    {
        this.emailAddresses = emailAddresses;
    }

    public List<DebugToolTestBean> getChildDummyBeans()
    {
        return childDummyBeans;
    }

    public void setChildDummyBeans(List<DebugToolTestBean> childDummyBeans)
    {
        this.childDummyBeans = childDummyBeans;
    }

    public String getFullName()
    {
        return (StringUtils.defaultIfEmpty(firstName, "") + " " + StringUtils.defaultString(lastName, "")).trim();
    }

    public void addChildDummyBean(DebugToolTestBean dummyBean)
    {
        if (childDummyBeans == null)
            childDummyBeans = new ArrayList<DebugToolTestBean>();

        childDummyBeans.add(dummyBean);
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

}
