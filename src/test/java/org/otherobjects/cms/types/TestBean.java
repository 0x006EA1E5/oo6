package org.otherobjects.cms.types;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type(label = "TestBean", description = "An annotated test bean", labelProperty = "name")
public class TestBean
{
    private Long id;
    private String name;
    private Date dob;
    private List<String> others;

    // Inferred-type properties
    private Long number;
    private BigDecimal decimal;
    private Date date;
    private String string;
    private Boolean bool;
    private CmsImage reference;
    
    
    public TestBean()
    {

    }

    @Property(order = 1, description = "The id", label = "id", required = true, type = PropertyType.NUMBER)
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @Property(order = 2, description = "The name", required = true, type = PropertyType.STRING)
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Property(order = 3, description = "The dob", label = "dob", required = false)
    public Date getDob()
    {
        return dob;
    }

    public void setDob(Date dob)
    {
        this.dob = dob;
    }

    @Property(order = 4, description = "The others", label = "others", required = false, type = PropertyType.LIST, collectionElementType = PropertyType.STRING)
    public List<String> getOthers()
    {
        return others;
    }

    public void setOthers(List<String> others)
    {
        this.others = others;
    }

    @Property(order = 100)
    public Long getNumber()
    {
        return number;
    }

    public void setNumber(Long number)
    {
        this.number = number;
    }

    @Property(order = 110)
    public BigDecimal getDecimal()
    {
        return decimal;
    }

    public void setDecimal(BigDecimal decimal)
    {
        this.decimal = decimal;
    }

    @Property(order = 120)
    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    @Property(order = 130)
    public String getString()
    {
        return string;
    }

    public void setString(String string)
    {
        this.string = string;
    }

    @Property(order = 140)
    public Boolean getBool()
    {
        return bool;
    }

    public void setBool(Boolean bool)
    {
        this.bool = bool;
    }

    @Property(order = 150)
    public CmsImage getReference()
    {
        return reference;
    }

    public void setReference(CmsImage reference)
    {
        this.reference = reference;
    }

}
