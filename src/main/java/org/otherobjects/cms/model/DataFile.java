package org.otherobjects.cms.model;

import java.io.File;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.activation.MimetypesFileTypeMap;

import org.otherobjects.cms.OtherObjectsException;
import org.springframework.util.Assert;

/**
 * Represents a file stored in the data repository.
 * 
 * <p>TODO Resolve naming of security levels.
 * 
 * <ol>
 * <li>Public file
 * <li>Public file over SSL
 * <li>Public file but with restricted access
 * <li>Private file
 * <li>Protected file can not be modified (potential CMS concept)
 * </ol>
 * 
 * @author rich
 */
public class DataFile
{
    // Todo move to Dao?
    private static final String DEFAULT_COLLECTION_NAME = "default";

    // Collection name
    private String collection = DEFAULT_COLLECTION_NAME;
    private String path;
    private String fileName;

    private File file;
    //private InputStream dataStream;

    /** True is this file in not be made externally available. */
    private boolean internalOnly = false;

    // TODO Is this better as a URL?
    private String externalUrl;
    private long fileSize;

    public DataFile()
    {
    }

    public DataFile(File file)
    {
        setFile(file);
        setFileName(file.getName());
        // TODO Should this be calculated on the fly?
        setFileSize(file.length());
    }

    public void setId(String id)
    {
        int firstSlash = id.indexOf("/", 1);
        int lastSlash = id.lastIndexOf("/");

        setCollection(id.substring(1, firstSlash));
        if (firstSlash != lastSlash)
        {
            // There is a path
            setPath(id.substring(firstSlash, lastSlash + 1));
        }
    }

    public String getId()
    {
        return "/" + getCollection() + (getPath() != null ? getPath().substring(0, getPath().length() - 1) : "") + "/" + getFileName();
    }

    public MimeType getMimeType()
    {
        // FIXME Use the file data instead or validate content type on file save
        try
        {
            return new MimeType(new MimetypesFileTypeMap().getContentType(getFileName()));
        }
        catch (MimeTypeParseException e)
        {
            throw new OtherObjectsException("Could not determine mimeType from file: " + getFileName());
        }
    }

    public InputStream getDataStream()
    {
        return null;
    }

    public boolean isInternalOnly()
    {
        return this.internalOnly;
    }

    public void setInternalOnly(boolean internalOnly)
    {
        this.internalOnly = internalOnly;
    }

    public long getFileSize()
    {
        return this.fileSize;
    }

    public void setFileSize(long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getExternalUrl()
    {
        return this.externalUrl;
    }

    public void setExternalUrl(String externalUrl)
    {
        this.externalUrl = externalUrl;
    }

    public String getPath()
    {
        return this.path;
    }

    /** 
     * Internal collection path of file. Must start and end with a slash.
     * 
     * @return
     */
    public void setPath(String path)
    {
        if (path != null)
        {
            Assert.isTrue(path.startsWith("/"), "Path must start and end with a '/': " + path);
            Assert.isTrue(path.endsWith("/"), "Path must start and end with a '/': " + path);
        }
        this.path = path;
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public File getFile()
    {
        return this.file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public String getCollection()
    {
        return this.collection;
    }

    /** 
     * Collection that this file belongs to. Must NOT contain a slash.
     * 
     * @return
     */
    public void setCollection(String collection)
    {
        if (collection != null)
        {
            Assert.isTrue(!collection.contains("/"));
        }
        this.collection = collection;
    }
}
