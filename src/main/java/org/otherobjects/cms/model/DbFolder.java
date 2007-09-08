package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.TypeService;

import flexjson.JSON;

@SuppressWarnings("unchecked")
public class DbFolder extends DynaNode implements Folder {

	private String label;
	private String mainType;
	private String mainTypeQuery;
	private String cssClass;
	
    //FIXME This List should be TypeDefs but they are really DynaNodes...
    private List allowedTypes;

    public List getAllAllowedTypes()
    {
        if (getAllowedTypes() != null && getAllowedTypes().size() > 0)
            return getAllowedTypes();
        else
            return (List) ((TypeService)SingletonBeanLocator.getBean("typeService")).getTypesBySuperClass(DynaNode.class);
    }

    @JSON(include = false)
    public List getAllowedTypes()
    {
        return allowedTypes;
    }

    public void setAllowedTypes(List allowedTypes)
    {
        this.allowedTypes = allowedTypes;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

	public String getMainType() {
		return mainType;
	}

	public void setMainType(String mainType) {
		this.mainType = mainType;
	}

	public String getMainTypeQuery() {
		return mainTypeQuery;
	}

	public void setMainTypeQuery(String mainTypeQuery) {
		this.mainTypeQuery = mainTypeQuery;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

}
