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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.otherobjects.cms.util.StringUtils;

public class OoResourceMetaDataHelper
{
    public final static String DEFAULT_FILE_ENCODING = "UTF-8";
    public final static Character DEFAULT_LINE_DELIMITER = '\n';

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
                        LineIterator it = IOUtils.lineIterator(is, DEFAULT_FILE_ENCODING); //FIXME is it save to assume we only ever read UTF-8 files? And what about line separator chars?
                        // get the first line
                        String line = it.nextLine();

                        if (line.startsWith(blockCommentSpec.getBlockStart()))
                        {
                            Pattern pattern = Pattern.compile("\\s*" + blockCommentSpec.getBlockStartPattern() + "\\s*(.*?)\\s*" + blockCommentSpec.getBlockEndPattern() + "\\s*");
                            Matcher matcher = pattern.matcher(line);
                            if (matcher.matches())
                                result = matcher.group(1);
                        }
                    }
                }
            }
            catch (Exception e)
            {

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
