package org.otherobjects.cms.model;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

/**
 * Selects a list of items via a query. This is mapped as a Type to allow saving in the repository
 * but this can be used on its own to generate queries easily.
 * 
 * FIXME Validation rule on this needed. Eg path must start and end in slash
 * FIXME Support notion of current folder/object.
 * 
 * TODO Add support for manual selection/override
 * TODO Add support for script to create selection
 * TODO Date selection
 *
 * @author rich
 */
@Type
public class Selector extends BaseNode
{
    private String label;
    private String description;
    private String queryPath;
    private Boolean recursive = false;
    private String queryTypeName;
    private String customQuery;
    private Long start;
    private Long end;

    @Property(order = 10)
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(order = 20)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Property(order = 30)
    public String getQueryPath()
    {
        return queryPath;
    }

    public void setQueryPath(String queryPath)
    {
        this.queryPath = queryPath;
    }

    @Property(order = 35)
    public Boolean getRecursive()
    {
        return recursive;
    }

    public void setRecursive(Boolean recursive)
    {
        this.recursive = recursive;
    }

    @Property(order = 40)
    public String getQueryTypeName()
    {
        return queryTypeName;
    }

    public void setQueryTypeName(String queryTypeName)
    {
        this.queryTypeName = queryTypeName;
    }

    @Property(order = 50)
    public String getCustomQuery()
    {
        return customQuery;
    }

    public void setCustomQuery(String customQuery)
    {
        this.customQuery = customQuery;
    }

    @Property(order = 60)
    public Long getStart()
    {
        return start;
    }

    public void setStart(Long start)
    {
        this.start = start;
    }

    @Property(order = 70)
    public Long getEnd()
    {
        return end;
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
            if (getRecursive())
                query.append("/"); // Add extra slast to query to allow recursion
            query.append("*");
        }
        else
            query.append("//*");

        // Construct type query
        if (StringUtils.isNotBlank(getQueryTypeName()))
        {
            // TODO Validate that typeName is a real type
            query.append(" [@ooType='" + getQueryTypeName() + "']");
        }
        return query.toString();
    }
}
