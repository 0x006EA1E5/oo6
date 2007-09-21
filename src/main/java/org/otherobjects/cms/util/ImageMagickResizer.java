package org.otherobjects.cms.util;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Are paths an issue on Windows/Linux?
 * 
 * <p><strong>Class depends on an installed version of ImageMagick</strong>
 */
public class ImageMagickResizer implements ImageResizer
{
    private static final int DEFAULT_QUALITY = 75;
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(255, 255, 255); // white
    private static final boolean PROGRESSIVE_INTERLACING = false;
    private final String commandPath = "/opt/local/bin/";
    private final Logger logger = LoggerFactory.getLogger(ImageMagickResizer.class);

    public void resize(File original, File destination, Integer targetWidth, Integer targetHeight, Color backgroundColor, Integer quality)
    {
        List<String> command = new ArrayList<String>();

        try
        {
            // Default background color if not specified
            if (backgroundColor == null)
                backgroundColor = DEFAULT_BACKGROUND_COLOR;

            // Check quality and use default if necessary
            if (quality == null)
                quality = DEFAULT_QUALITY;
            else if (quality < 0 || quality > 100)
            {
                this.logger.warn("Quality of {} is outside allowed range of 0-100. Using default of {}.", quality, DEFAULT_QUALITY);
                quality = DEFAULT_QUALITY;
            }

            // Example command structure:
            // convert -size 300x300 -thumbnail 100x100 -bordercolor white  -border 50 -gravity center -crop 100x100+0+0 +repage  2974588.jpeg thumb.jpg
            // See http://www.imagemagick.org/Usage/thumbnails/ for info

            // Make desintation directory if required
            destination.getParentFile().mkdirs();

            // Determine appropriate dimensions
            command.add(this.commandPath + "convert");

            // Turn on interlacing
            if (PROGRESSIVE_INTERLACING)
            {
                command.add("-interlace");
                command.add("Line");
            }

            // Determine sizing
            StringBuffer size = new StringBuffer();
            boolean requiresCanvas = false;
            if (targetHeight == null)
            {
                // Fixed width resize eg 400x
                size.append(targetWidth.toString() + "x");
            }
            else if (targetWidth == null)
            {
                // Fixed width resize eg 400x
                size.append("x" + targetHeight.toString());
            }
            else
            {
                // Fixed wdith and height resize so we'll need a background canvas
                requiresCanvas = true;
            }

            if (!requiresCanvas)
            {
                // No canvas so a simple resize will do
                command.add("-geometry");
                command.add(size.toString());
            }
            else
            {
                // 6.3 only method
//            	command.add("-size");
//            	command.add(targetWidth * 3 + "x" + targetHeight * 3);
//            	command.add("-thumbnail");
//            	command.add(targetWidth + "x" + targetHeight + ">");
//            	command.add("-bordercolor");
//            	command.add("rgba(" + backgroundColor.getRed() + "," + backgroundColor.getBlue() + "," + backgroundColor.getGreen() + "," + backgroundColor.getAlpha() + ")");
//            	command.add("-border");
//            	command.add("" + targetWidth / 2);
//            	command.add("-gravity");
//            	command.add("center");
//            	command.add("-extent");
//            	command.add(targetWidth + "x" + targetHeight);
            	
            	// 6.2 compatible method
                command.add("-size");
                command.add(targetWidth * 3 + "x" + targetHeight * 3);
                command.add("-thumbnail");
                command.add(targetWidth + "x" + targetHeight + ">");
                command.add("-bordercolor");
                command.add("rgba(" + backgroundColor.getRed() + "," + backgroundColor.getBlue() + "," + backgroundColor.getGreen() + "," + backgroundColor.getAlpha() + ")");
                command.add("-border");
                command.add("" + targetWidth / 2);
                command.add("-gravity");
                command.add("center");
                command.add("-crop");
                command.add(targetWidth + "x" + targetHeight+"+0+0");
                command.add("+repage");
            }

            // Set qualtity
            command.add("-quality");
            command.add("" + quality);

            // Set filenames
            command.add(original.getAbsolutePath());
            command.add(destination.getAbsolutePath());

            // Execute command
            execCommand(command.toArray(new String[]{}));
            return;

        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not resize image: " + original, e);
        }
    }

    protected int execCommand(String[] command)
    {
        System.err.println("Executing image resize command: " + StringUtils.join(command, " "));
        this.logger.debug("Executing image resize command: {}", StringUtils.join(command, " "));

        Process proc;
        try
        {
            proc = Runtime.getRuntime().exec(command);
            int exitStatus = proc.waitFor();
            String output = IOUtils.toString(proc.getInputStream());
            this.logger.debug("Image resize command status: {}", exitStatus);
            this.logger.debug("Image resize command output: {}", output);
            if (exitStatus != 0)
            {
                String error = IOUtils.toString(proc.getErrorStream());
                throw new OtherObjectsException("Error whilst executing image resizer command: " + error);
            }
            else
                return exitStatus;
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not create process object required by image resizer.", e);
        }
    }
}
