package org.otherobjects.cms.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.springframework.util.Assert;

public class JcrPathTool
{
    /**
     * Iterator returning the various parts of a "/" limited path expression, walking from the leaf
     * part up to the root.<br/>
     * Fails on relative paths (i.e. those not starting with "/")<br/>
     * Will return an 'empty' Iterator (i.e one whose hasNext() method return false) when asked to scan the root path "/"
     * 
     * @param path
     * @return Iterator of {@link JcrPathTool.PathItem}s
     */
    public static Iterator<PathItem> walkUpPath(String path)
    {
        if (StringUtils.isBlank(path))
            return null;

        Assert.isTrue(path.startsWith("/"), "Path must start with a slash");

        List<PathItem> parts = new ArrayList<PathItem>();
        Scanner scanner = new Scanner(path).useDelimiter("/");
        StringBuffer pathUpToHere = new StringBuffer("/");
        while (scanner.hasNext())
        {
            String pathPart = scanner.next();
            pathUpToHere.append(pathPart);

            PathItem pathItem = new PathItem(pathPart);
            if (scanner.hasNext())
            {
                pathItem.setFolder(true);
                pathUpToHere.append("/");
            }
            else
            {
                pathItem.setLast(true);
                if (!pathPart.contains(".") || path.trim().endsWith("/"))
                {
                    pathItem.setFolder(true);
                    pathUpToHere.append("/");
                }
            }
            pathItem.setPathUpToHere(pathUpToHere.toString());
            parts.add(pathItem);
        }

        Collections.reverse(parts);
        return parts.iterator();
    }

    public static class PathItem
    {
        private boolean isLast = false;
        private boolean isFolder = false;
        private String pathUpToHere;
        private String pathPart;

        public PathItem(String pathUpToHere, String pathPart)
        {
            this(pathPart);
            this.pathUpToHere = pathUpToHere;
        }

        public PathItem(String pathPart)
        {
            this.pathPart = pathPart;
        }

        public boolean isLast()
        {
            return isLast;
        }

        public void setLast(boolean isLast)
        {
            this.isLast = isLast;
        }

        public boolean isFolder()
        {
            return isFolder;
        }

        public void setFolder(boolean isFolder)
        {
            this.isFolder = isFolder;
        }

        public String getPathUpToHere()
        {
            return pathUpToHere;
        }

        public void setPathUpToHere(String pathUpToHere)
        {
            this.pathUpToHere = pathUpToHere;
        }

        public String getPathPart()
        {
            return pathPart;
        }

        public void setPathPart(String pathPart)
        {
            this.pathPart = pathPart;
        }

        @Override
        public String toString()
        {
            return String.format("pathUpToHere: '%s', part:[%s], folder?: %s, last?: %s", pathUpToHere, pathPart, isFolder, isLast);
        }
    }
}
