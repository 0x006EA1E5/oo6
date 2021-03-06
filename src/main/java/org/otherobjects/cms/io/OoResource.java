package org.otherobjects.cms.io;

import java.io.IOException;
import java.io.OutputStream;

import org.otherobjects.cms.Url;
import org.springframework.core.io.Resource;

/**
 * Simple extension to Spring's {@link Resource} which adds methods to write to a file and find out whether you should be able to do so
 * and get to a resource's metadata.
 * @author joerg
 *
 */
public interface OoResource extends Resource
{
    /**
     * Get an outputstream on this resource
     * @return outputstream
     * @throws IOException
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Get the virtual path that this resource lives on. See {@link OoResourcePathPrefix} for possible path prefixes
     * @return
     */
    String getPath();

    /**
     * Find out whether this resource can be written to or whether it is one of those that live in read-only places
     * @return
     */
    boolean isWritable();

    /**
     * Get the resource's metaData or null if this resource doesn't have any associated metadata
     * @return
     */
    OoResourceMetaData getMetaData();

    /**
     * Get a {@link Url} object if one can be calculated for this resource or null if it can't
     * @return
     */
    Url getUrl();

    /**
     * get the prefix enum value
     * @return
     */
    OoResourcePathPrefix getPrefix();

}
