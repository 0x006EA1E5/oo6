package org.otherobjects.cms.model;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

/**
 * Selects a list of items via a query. This is mapped as a Type to allow saving in the repository
 * but this can be used on its own to generate queries easily.
 * 
 * FIXME !!! Validation rule on this needed. Eg path must start and end in slash
 * FIXME Support notion of current folder/object.
 * 
 * TODO Add support for manual selection/override
 * TODO Add support for script to create selection
 * TODO Date selection
 *
 * @author rich
 */
@Type(codeProperty="")
public class Selector extends BaseComponent
{
    private String queryPath;
    private String queryTypeName;
    private String queryTags;

    private String orderBy;

    private Boolean random = false;
    private Boolean subFolders = false;
    private String customQuery;
    private Long start;
    private Long end;

    @Property(order = 30)
    public String getQueryPath()
    {
        return this.queryPath;
    }

    public void setQueryPath(String queryPath)
    {
        if (queryPath != null && !queryPath.endsWith("/"))
        {
            queryPath = queryPath + "/"; // Path queries must end in slash
        }
        this.queryPath = queryPath;
    }

    @Property(order = 35)
    public String getQueryTags()
    {
        return this.queryTags;
    }

    public void setQueryTags(String queryTags)
    {
        this.queryTags = queryTags;
    }

    @Property(order = 34, label = "Random?")
    public Boolean getRandom()
    {
        return this.random;
    }

    public void setRandom(Boolean random)
    {
        this.random = random;
    }

    @Property(order = 35, label = "Sub folders?")
    public Boolean getSubFolders()
    {
        return this.subFolders;
    }

    public void setSubFolders(Boolean subFolders)
    {
        this.subFolders = subFolders;
    }

    @Property(order = 40)
    public String getQueryTypeName()
    {
        return this.queryTypeName;
    }

    public void setQueryTypeName(String queryTypeName)
    {
        this.queryTypeName = queryTypeName;
    }

    @Property(order = 41)
    public String getOrderBy()
    {
        return this.orderBy;
    }

    public void setOrderBy(String orderBy)
    {
        this.orderBy = orderBy;
    }

    @Property(order = 50)
    public String getCustomQuery()
    {
        return this.customQuery;
    }

    public void setCustomQuery(String customQuery)
    {
        this.customQuery = customQuery;
    }

    @Property(order = 60)
    public Long getStart()
    {
        return this.start;
    }

    public void setStart(Long start)
    {
        this.start = start;
    }

    @Property(order = 70)
    public Long getEnd()
    {
        return this.end;
    }

    public void setEnd(Long end)
    {
        this.end = end;
    }

    /**
     * Return the query expression for this selector.
     * 
     * TODO Move to StringBuilder
     * 
     * @return
     */
    public String getQuery()
    {
        if (StringUtils.isNotBlank(getCustomQuery()))
            return getCustomQuery();

        // Contsruct path
        StringBuilder query = new StringBuilder("/jcr:root");
        if (StringUtils.isNotBlank(getQueryPath()))
        {
            query.append(getQueryPath());
            if (getSubFolders() != null && getSubFolders())
            {
                query.append("/"); // Add extra slast to query to allow recursion
            }
            query.append("*");
        }
        else
        {
            query.append("//*");
        }

        // Construct type query
        if (StringUtils.isNotBlank(getQueryTypeName()))
        {
            if (getQueryTypeName().contains("%"))
            {
                // Support wildcard matches
                query.append(" [jcr:like(@ooType,'" + getQueryTypeName() + "')]");
            }
            else
            {
                // TODO Validate that typeName is a real type
                query.append(" [@ooType='" + getQueryTypeName() + "']");
            }
        }

        if (StringUtils.isNotBlank(getOrderBy()))
        {
            query.append(" order by " + getOrderBy());
        }

        // Add custom OO selector
        String range = "";
        if (getEnd() != null)
        {
            if (getStart() != null)
            {
                range = " {" + getStart() + ".." + getEnd() + "}";
            }
            else if (getRandom())
            {
                range = " {%" + getEnd() + "}";
            }
            else
            {
                range = " {" + getEnd() + "}";
            }
        }
        query.append(range);

        return query.toString();
    }

}
