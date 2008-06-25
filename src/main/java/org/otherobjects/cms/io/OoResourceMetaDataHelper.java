package org.otherobjects.cms.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.otherobjects.cms.util.StringUtils;

/**
 * Helper class to read and write a JSON metadata string from and to the first line of an {@link OoResource} file. 
 * It takes into account comment syntax for the given filetype and neither reads nor writes the metadata string, if it doesn't 
 * have sufficient knowlege about the given file type.
 * 
 * It assumes {@link OoResourceMetaDataHelper#DEFAULT_FILE_ENCODING} and {@link OoResourceMetaDataHelper#DEFAULT_LINE_DELIMITER} when 
 * reading and writing file contents and
 * 
 * @author joerg
 *
 */
public class OoResourceMetaDataHelper
{
    public static final String DEFAULT_FILE_ENCODING = "UTF-8";
    public static final Character DEFAULT_LINE_DELIMITER = '\n';

    private static MimetypesFileTypeMap mimetypes = new MimetypesFileTypeMap();

    private static Map<String, BlockCommentSpec> commentSpecByMimetype = new HashMap<String, BlockCommentSpec>();

    {
        commentSpecByMimetype.put("text/freemarker", new BlockCommentSpec("<#--", "-->"));
        commentSpecByMimetype.put("text/velocity", new BlockCommentSpec("#*", "#\\*", "*#", "\\*#"));
        commentSpecByMimetype.put("text/html", new BlockCommentSpec("<!--", "-->"));
        commentSpecByMimetype.put("text/css", new BlockCommentSpec("/*", "/\\*", "*/", "\\*/"));
        commentSpecByMimetype.put("text/javascript", new BlockCommentSpec("/*", "/\\*", "*/", "\\*/"));
    }

    /**
     * 
     * @param resource
     * @return metaData Json String or null if one can't be read from the given resource.
     */
    public String readMetaDataString(OoResource resource)
    {
        String result = null;
        InputStream is = null;
        if (resource.exists())
        {
            try
            {
                String contentType = mimetypes.getContentType(resource.getFilename());
                // only try and extract metadata line from text files
                if (!contentType.equals("application/octet-stream") && contentType.startsWith("text"))
                {
                    BlockCommentSpec blockCommentSpec = commentSpecByMimetype.get(contentType);
                    if (blockCommentSpec != null) // only do something if we know how comments are formatted
                    {
                        is = resource.getInputStream();
                        String string = IOUtils.toString(is);
                        
                        // TODO Don't process non-JSON strings
                        if(!string.contains("{"))
                            return null;
                        
                        int startPos = string.indexOf(blockCommentSpec.getBlockStart());
                        if (startPos >= 0)
                        {
                            startPos += blockCommentSpec.getBlockStart().length();
                            int endPos = string.indexOf(blockCommentSpec.getBlockEnd());
                            result = string.substring(startPos, endPos);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Explain why we ignore exception
            }
            finally
            {
                IOUtils.closeQuietly(is);
            }
        }
        return result;
    }

    public void writeMetaDataString(OoResource resource, String metaData) throws IOException
    {
        if (StringUtils.isBlank(metaData))
            return;

        if (!resource.isWritable())
            throw new IOException("This resource is not writable");

        String contentType = mimetypes.getContentType(resource.getFilename());
        BlockCommentSpec blockCommentSpec = null;
        if (!contentType.equals("application/octet-stream") && contentType.startsWith("text"))
        {
            blockCommentSpec = commentSpecByMimetype.get(contentType);
        }

        if (blockCommentSpec == null)
            return;

        StringBuffer buf = new StringBuffer();
        buf.append(blockCommentSpec.getBlockStart());
        buf.append(metaData);
        buf.append(blockCommentSpec.getBlockEnd());
        buf.append(DEFAULT_LINE_DELIMITER);

        String metaDataHeader = buf.toString();

        File tempFile = null;
        File original = resource.getFile();
        boolean tempFileCreationSuccessfull = false;
        OutputStreamWriter osw = null;
        InputStreamReader isr = null;
        try
        {
            tempFile = File.createTempFile("meta", ".txt");
            osw = new OutputStreamWriter(new FileOutputStream(tempFile, true), DEFAULT_FILE_ENCODING);
            isr = new InputStreamReader(new FileInputStream(original), DEFAULT_FILE_ENCODING);

            // write header
            IOUtils.write(metaDataHeader, osw);

            // copy originalData
            IOUtils.copy(isr, osw);

            tempFileCreationSuccessfull = true;
        }
        finally
        {
            IOUtils.closeQuietly(isr);
            IOUtils.closeQuietly(osw);
            if (!tempFileCreationSuccessfull)
                FileUtils.deleteQuietly(tempFile);
        }

        // now copy temp file over original file
        if (tempFileCreationSuccessfull)
        {
            try
            {
                FileUtils.copyFile(tempFile, original, false);
            }
            finally
            {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }

    class BlockCommentSpec
    {
        private String blockStart;
        private String blockStartPattern;
        private String blockEnd;
        private String blockEndPattern;

        public BlockCommentSpec(String blockStart, String blockEnd)
        {
            this.blockStart = blockStart;
            this.blockStartPattern = blockStart;
            this.blockEnd = blockEnd;
            this.blockEndPattern = blockEnd;
        }

        public BlockCommentSpec(String blockStart, String blockStartPattern, String blockEnd, String blockEndPattern)
        {
            this.blockStart = blockStart;
            this.blockStartPattern = blockStartPattern;
            this.blockEnd = blockEnd;
            this.blockEndPattern = blockEndPattern;
        }

        public String getBlockStart()
        {
            return blockStart;
        }

        public String getBlockEnd()
        {
            return blockEnd;
        }

        public String getBlockStartPattern()
        {
            return blockStartPattern;
        }

        public String getBlockEndPattern()
        {
            return blockEndPattern;
        }

    }
}
