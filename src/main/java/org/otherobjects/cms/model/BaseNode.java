package org.otherobjects.cms.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.Url;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.StringUtils;
import org.otherobjects.framework.OtherObjectsException;
import org.otherobjects.framework.SingletonBeanLocator;
import org.springframework.util.Assert;

/**
 * Document those property names that can't be used. Better still name space them?
 * 
 * FIXME Our standard props should be namespaced eg ooLabel?
 * 
 * @author rich
 */
public abstract class BaseNode implements CmsNode, Audited, Editable, Linkable, Serializable
{
    /** Default icon path. */
    private static final String ICON_PATH = "/otherobjects/static/icons/page.png";

    /** GUID */
    private String id;

    /** The path of this node's parent. Must end with a forward slash. Ignored when this node is a component. */
    private String path;

    /** System readable identifier eg filename. Used an the name of the node. Must not contain a slash. */
    protected String code;

    /** Indication of whether this node is published or not. */
    private boolean published = false;

    /** FIXME Why store this? Indication of whether this node is a folder or not. */
    private boolean folder = false;

    private String creator;
    private Date creationTimestamp;
    private String modifier;
    private Date modificationTimestamp;
    private String editingComment;
    private int version = 0;

    /** Tag list. */
    private String ooTags;

    private TypeDef typeDef;

    public BaseNode()
    {
    }

    @Deprecated
    public Url getHref()
    {
        // FIXME This should be in SitePage
        return new Url(getLinkPath());
    }

    /**
     * FIXME Temp hack to sort out type labels in grid
     */
    public String getTypeLabel()
    {
        return getTypeDef().getLabel();
    }

    /**
     * Returns the human readable label for this object.
     * 
     * <p>The property that is read is specified by <code>labelProperty</code>;
     * 
     * @return
     */
    public String getOoLabel()
    {
        Assert.notNull(getLabelProperty(), "Could not get label since labelProperty is not set for: " + getOoType());
        return String.valueOf(getPropertyValue(getLabelProperty()));
    }

    /**
     * Returns an image that represents this object.
     * 
     * <p>The property that is read is specified by <code>imageProperty</code>;
     * 
     * @return
     */
    public CmsNode getOoImage()
    {
        if (StringUtils.isEmpty(getTypeDef().getImageProperty()))
            return null;
        else if (getTypeDef().getImageProperty().equals("self"))
            return this;
        else
            return (CmsNode) getPropertyValue(getTypeDef().getImageProperty());
    }

    /**
     * Sets the human readable label for this object.
     * 
     * <p>The property that is set is specified by <code>labelProperty</code>;
     * 
     * @return
     */
    public void setOoLabel(String label)
    {
        Assert.notNull(getLabelProperty(), "Could not set label since labelProperty is not set for: " + getOoType());
        setPropertyValue(getLabelProperty(), label);
    }

    /**
     * Returns the path to an icon representing this object.
     * 
     * @return
     */
    public String getOoIcon()
    {
        return ICON_PATH;
    }

    /**
     * Returns a description of this object.
     * 
     * @return
     */
    public String getOoDescription()
    {
        return null;
    }

    /**
     * Return the path of this node for use with JCR. The jcr path is comprised of
     * the path and the code.
     * 
     * @return the jcr path
     */
    public String getJcrPath()
    {
        // FIXME This means that this may not always be acurate. Make sure this ALWAYS returns true jcr path
        if (getPath() == null || getPath() == null)
            return null;

        Assert.isTrue(this.path.endsWith("/"), "Path must end with a forward slash");
        return getPath() + getCode();
    }

    /**
     * Sets the JCR path for this node. The path and code will be inferred form this.
     * 
     * @param jcrPath
     */
    public void setJcrPath(String jcrPath)
    {
        Assert.notNull(jcrPath, "jcrPath must not be null");

        Assert.isTrue(jcrPath.lastIndexOf("/") >= 0, "jcrPath must contain at least one forward slash");
        Assert.isTrue(!jcrPath.endsWith("/"), "jcrPath must not end with a forward slash");

        int slashPos = jcrPath.lastIndexOf("/");
        setPath(jcrPath.substring(0, slashPos + 1));
        setCode(jcrPath.substring(slashPos + 1));
    }

    public Object getPropertyValue(String name)
    {
        // FIXME Need consistent method for accessing properties Spring vs BeanWrapper
        try
        {
            return PropertyUtils.getProperty(this, name);
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not get property value for '" + name + "' in: " + getTypeDef().getName(), e);
        }
    }

    public boolean hasProperty(String name)
    {
        // FIXME Need consistent method for accessing properties Spring vs BeanWrapper
        try
        {
            PropertyUtils.getProperty(this, name);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public void setPropertyValue(String name, Object value)
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
        return "[" + getOoType() + "] " + getOoLabel();
    }

    public String getId()
    {
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPath()
    {
        return this.path;
    }

    public void setPath(String path)
    {
        Assert.notNull(path, "path may not be null.");
        // FIXME       Assert.doesNotContain(path, ".", "path may not contain a period.");
        if (!path.endsWith("/"))
            path += "/";
        this.path = path;
    }

    public void setLabel(String label)
    {
        setOoLabel(label);
    }

    public String getCode()
    {
        return this.code != null ? this.code : StringUtils.generateUrlCode(getOoLabel());
    }

    public void setCode(String code)
    {
        Assert.notNull(code, "code may not be null.");
        Assert.doesNotContain(code, "/", "code may not contain a slash: " + code);
        this.code = code;
    }

    public String getOoType()
    {
        return getClass().getName();
    }

    public void setOoType(String ooType)
    {
        // FIXME Just to make this look like a property for ocm
    }

    /**
     * FIXME Better name needed?
     * @deprecated Use getOoUrlPath() instead
     */
    public String getLinkPath()
    {
        if (getJcrPath() == null)
            return null;

        String linkPath = getJcrPath().replaceAll("^/site", "");
        if (isFolder())
            linkPath += "/";
        return linkPath;
    }

    /**
     * Returns url path for this object.
     */
    public String getOoUrlPath()
    {
        if (getJcrPath() == null)
            return null;

        String linkPath = getJcrPath().replaceAll("^/site", "");
        if (isFolder())
            linkPath += "/";
        return linkPath;
    }

    public Url getOoUrl()
    {
        return new Url(getOoUrlPath());
    }

    public String getLabelProperty()
    {
        Assert.notNull(getTypeDef(), "No TypeDef found for object: " + getClass().getName());
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

    public boolean isPublished()
    {
        return this.published;
    }

    public void setPublished(boolean published)
    {
        this.published = published;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    public String getEditingComment()
    {
        return editingComment;
    }

    public void setEditingComment(String editingComment)
    {
        this.editingComment = editingComment;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public Date getModificationTimestamp()
    {
        return this.modificationTimestamp;
    }

    public void setModificationTimestamp(Date modificationTimestamp)
    {
        this.modificationTimestamp = modificationTimestamp;
    }

    public int getVersion()
    {
        return this.version;
    }

    public String getEditableId()
    {
        return getId();
    }

    public boolean isFolder()
    {
        return this.folder;
    }

    public void setFolder(boolean folder)
    {
        this.folder = folder;
    }

    public String getLabel()
    {
        return getOoLabel();
    }

    public void setTypeDef(TypeDef typeDef)
    {
        this.typeDef = typeDef;
    }

    public String getOoTags()
    {
        return ooTags;
    }

    public void setOoTags(String ooTags)
    {
        this.ooTags = ooTags;
    }

    public Date getCreationTimestamp()
    {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp)
    {
        this.creationTimestamp = creationTimestamp;
    }

    public<T extends BaseNode> BaseNode getParentNode(UniversalJcrDao universalJcrDao)
    {
        return universalJcrDao.getByPath(getPath());
    }

}
