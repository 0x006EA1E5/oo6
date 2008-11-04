package org.otherobjects.cms.util;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple logger that captures logs statements and formats wraps them as HTML
 *
 * @author rich
 */
public class HtmlLogger
{
    private final Logger logger = LoggerFactory.getLogger(HtmlLogger.class);

    private Writer outputWriter;

    public HtmlLogger(Writer writer)
    {
        outputWriter = writer;
    }

    public void debug(String message)
    {
        write("debug", message);
    }

    public void info(String message)
    {
        write("info", message);
    }

    public void warn(String message)
    {
        write("warn", message);
    }

    public void error(String message)
    {
        write("error", message);
    }

    private void write(String level, String message)
    {
        try
        {
            if (level.equals("debug"))
            {
                logger.debug(message);
                outputWriter.write("<span style=\"color:grey;\">" + message + "</span><br/>\n");
            }
            else if (level.equals("info"))
            {
                logger.info(message);
                outputWriter.write("<span style=\"color:green;\">" + message + "</span><br/>\n");
            }
            else if (level.equals("warn"))
            {
                logger.warn(message);
                outputWriter.write("<span style=\"color:orange;\">" + message + "</span><br/>\n");
            }
            else if (level.equals("error"))
            {
                logger.error(message);
                outputWriter.write("<span style=\"color:red;\">" + message + "</span><br/>\n");
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
        }
    }
}
