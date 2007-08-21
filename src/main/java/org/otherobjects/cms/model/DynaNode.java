package org.otherobjects.cms.model;

import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.StringUtils;
import org.otherobjects.cms.workbench.WorkbenchItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * A dynamic object represents a data node in the content repository.
 * 
 * <p>Dynamic nodes can change their properties and validators at runtime
 * providing a very flexible data model.
 * 
 * <p>TODO Add support for description, icon and image generators
 * <br>TODO Equals, hashCode, serialableId builders
 */
@SuppressWarnings("unchecked")
//TODO @Configurable("dynaNode")
public class DynaNode implements CmsNode, WorkbenchItem, AuditInfo, Editable
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** GUID */
    private String id;

    /** Property to be used an the label (human friendly identifier) for this node. */
    // TODO This comes from TypeDef now: private String labelProperty;
    /** The path of this node's parent. Must end with a forward slash. Ignored when this node is a component. */
    private String path;

    /** System readable identifier eg filename. Used an the name of the node. Must not contain a slash. */
    private String code;

    /* Additional textual information about this item */
    /* Icon representing this node */
    /* Image representing this node */

    /** The defining type for this node */
    private String ooType;
    
    /** Indication of whether this node is published or not */
    private boolean published = false;
    
    // Audit properties
    private String userName;
    private String userId;
    private Date modificationTimestamp;
    private String comment;
    private int changeNumber = 0;

    private TypeDef typeDef;

    public DynaNode(TypeDef typeDef)
    {
        setTypeDef(typeDef);
    }

    public DynaNode()
    {
    }

    /**
     * Creates a node of the specified type. The type definition 
     * in required to ensure that this node conforms.
     * 
     * @param type the type name
     */
    public DynaNode(String type)
    {
        setOoType(type);
    }

    public void setOoType(String ooType)
    {
        this.ooType = ooType;
    }

    /**
     * Return the path of this node for use with JCR. The jcr path is comprised of
     * the path and the code.
     * 
     * @return the jcr path
     */
    public String getJcrPath()
    {
        if (getPath() == null || getPath() == null)
            return null;

        Assert.isTrue(path.endsWith("/"), "Path must end with a forward slash");
        return getPath() + getCode();
    }

    /**
     * Sets the JCR path for this node. The path and code will be inferred form this.
     * 
     * @param jcrPath
     */
    public void setJcrPath(String jcrPath)
    {
        //        if (jcrPath == null)
        //        {
        //            setCode(null);
        //            setPath(null);
        //            return;
        //        }
        Assert.notNull(jcrPath, "jcrPath must not be null");

        Assert.isTrue(jcrPath.lastIndexOf("/") >= 0, "jcrPath must contain at least one forward slash");
        Assert.isTrue(!jcrPath.endsWith("/"), "jcrPath must not end with a forward slash");

        int slashPos = jcrPath.lastIndexOf("/");
        setPath(jcrPath.substring(0, slashPos + 1));
        setCode(jcrPath.substring(slashPos + 1));
    }

    public Object get(String name)
    {
        try
        {
            return PropertyUtils.getProperty(this, name);
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not get property value for: " + name, e);
        }
    }

    public void set(String name, Object value)
    {
        try
        {
            PropertyUtils.setNestedProperty(this, name, value);
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not set property value for: " + name, e);
        }
    }

    @Override
    public String toString()
    {
        return "[" + getOoType() + "] " + getLabel();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        Assert.notNull(path, "path may not be null.");
        // FIXME       Assert.doesNotContain(path, ".", "path may not contain a period.");
        if (!path.endsWith("/"))
            path += "/";
        this.path = path;
    }

    public String getLabel()
    {
        return (String) (get(getLabelProperty()) != null ? get(getLabelProperty()) : getCode());
    }

    public void setLabel(String label)
    {
        try
        {
            PropertyUtils.setNestedProperty(this, getLabelProperty(), label);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Couldn't set label property", e);
        }
    }

    public String getCode()
    {
        return code != null ? code : StringUtils.generateUrlCode(getLabel());
    }

    public void setCode(String code)
    {
        Assert.notNull(code, "code may not be null.");
        Assert.doesNotContain(code, "/", "code may not contain a slash.");
        this.code = code;
    }

    public String getOoType()
    {
        return ooType;
    }

    /**
     * FIXME Better name needed?
     */
    public String getLinkPath()
    {
        if (getJcrPath() == null)
            return null;

        String linkPath = getJcrPath().replaceAll("/site/", "/go/");
        if (!linkPath.contains(".") && !linkPath.endsWith("/"))
            linkPath += "/";
        return linkPath;
    }

    public String getLabelProperty()
    {
        return getTypeDef().getLabelProperty();
    }

    public TypeDef getTypeDef()
    {
        if (this.typeDef == null)
        {
            TypeService typeService = (TypeService) SingletonBeanLocator.getBean("typeService");
            this.typeDef = typeService.getType(getOoType());
        }
        return this.typeDef;
    }

    public void setTypeDef(TypeDef typeDef)
    {
        this.typeDef = typeDef;
        setOoType(typeDef.getName());
    }

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getModificationTimestamp() {
		return modificationTimestamp;
	}

	public void setModificationTimestamp(Date modificationTimestamp) {
		this.modificationTimestamp = modificationTimestamp;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getChangeNumber() {
		return changeNumber;
	}

	public void setChangeNumber(int changeNumber) {
		this.changeNumber = changeNumber;
	}

	public String getEditableId() {
		return getId();
	}
    
    
}
