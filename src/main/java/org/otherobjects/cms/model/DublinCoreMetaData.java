package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

/**
 * Title
Creator
Subject
Description
Publisher
Contributor
Date
Type
Format
Identifier
Source
Language
Relation
Coverage
Rights

* DC Essential: Lang, Title, Subject, Description, Publisher, Rights
* DC Optional: Identifier, Creator, Contributor, Date Created, Date Modified, References, Replaces

http://dublincore.org/documents/dcmi-terms/
http://dublincore.org/documents/dcmes-xml/


Also : https://addons.mozilla.org/en-US/firefox/addon/528
 */
@Type
public class DublinCoreMetaData extends BaseNode
{
    private String title;
    private String description;
    private List<String> keywords;

    @Override
    public String getCode()
    {
        return "metaData";
    }

    @Property(order = 10)
    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Property(order = 20)
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Property(order = 30)
    public List<String> getKeywords()
    {
        return this.keywords;
    }

    public void setKeywords(List<String> keywords)
    {
        this.keywords = keywords;
    }

}
