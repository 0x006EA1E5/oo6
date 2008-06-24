package org.otherobjects.cms.util;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;

import org.otherobjects.cms.OtherObjectsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.media.jai.codec.SeekableStream;

/**
 * TODO Are paths an issue on Windows/Linux?
 * 
 * <p><strong>Class depends on an installed version of ImageMagick</strong>
 */
public class JAIImageResizer implements ImageResizer
{
    private static final int DEFAULT_QUALITY = 75;
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(255, 255, 255); // white
    private static final boolean PROGRESSIVE_INTERLACING = false;
    private final String commandPath = "/opt/local/bin/";
    private final Logger logger = LoggerFactory.getLogger(JAIImageResizer.class);

    public void resize(File original, File destination, Integer targetWidth, Integer targetHeight, Color backgroundColor, Integer quality)
    {
        //        List<String> command = new ArrayList<String>();
        //
        //        try
        //        {
        //            // Default background color if not specified
        //            if (backgroundColor == null)
        //                backgroundColor = DEFAULT_BACKGROUND_COLOR;
        //
        //            // Check quality and use default if necessary
        //            if (quality == null)
        //                quality = DEFAULT_QUALITY;
        //            else if (quality < 0 || quality > 100)
        //            {
        //                this.logger.warn("Quality of {} is outside allowed range of 0-100. Using default of {}.", quality, DEFAULT_QUALITY);
        //                quality = DEFAULT_QUALITY;
        //            }
        //
        //            // Example command structure:
        //            // v6.2: convert -size 300x300 -thumbnail 100x100 -bordercolor white  -border 50 -gravity center -crop 100x100+0+0 +repage  2974588.jpeg thumb.jpg
        //            // v6.3: convert -size 200x200 -thumbnail '100x100>' -background skyblue -gravity center -extent 100x100 big.jpg thumb.jpg
        //            // See http://www.imagemagick.org/Usage/thumbnails/ for info
        //
        //            // Make desintation directory if required
        //            destination.getParentFile().mkdirs();
        //
        //            // Determine appropriate dimensions
        //            command.add(this.commandPath + "convert");
        //
        //            // Turn on interlacing
        //            if (PROGRESSIVE_INTERLACING)
        //            {
        //                command.add("-interlace");
        //                command.add("Line");
        //            }
        //
        //            // Determine sizing
        //            StringBuffer size = new StringBuffer();
        //            boolean requiresCanvas = false;
        //            if (targetHeight == null)
        //            {
        //                // Fixed width resize eg 400x
        //                size.append(targetWidth.toString() + "x");
        //            }
        //            else if (targetWidth == null)
        //            {
        //                // Fixed width resize eg 400x
        //                size.append("x" + targetHeight.toString());
        //            }
        //            else
        //            {
        //                // Fixed wdith and height resize so we'll need a background canvas
        //                requiresCanvas = true;
        //            }
        //
        //            if (!requiresCanvas)
        //            {
        //                // No canvas so a simple resize will do
        //                command.add("-geometry");
        //                command.add(size.toString());
        //            }
        //            else
        //            {
        //                // 6.3 only method
        //                command.add("-size");
        //                command.add(targetWidth * 3 + "x" + targetHeight * 3);
        //                command.add("-thumbnail");
        //                command.add(targetWidth + "x" + targetHeight + ">");
        //                command.add("-bordercolor");
        //                command.add("rgba(" + backgroundColor.getRed() + "," + backgroundColor.getBlue() + "," + backgroundColor.getGreen() + "," + backgroundColor.getAlpha() + ")");
        //                command.add("-border");
        //                command.add("" + targetWidth / 2);
        //                command.add("-gravity");
        //                command.add("center");
        //                command.add("-extent");
        //                command.add(targetWidth + "x" + targetHeight);
        //
        //                // 6.2 compatible method (fails with sime GIFs)
        ////                command.add("-size");
        ////                command.add(targetWidth * 3 + "x" + targetHeight * 3);
        ////                command.add("-thumbnail");
        ////                command.add(targetWidth + "x" + targetHeight + ">");
        ////                command.add("-bordercolor");
        ////                command.add("rgba(" + backgroundColor.getRed() + "," + backgroundColor.getBlue() + "," + backgroundColor.getGreen() + "," + backgroundColor.getAlpha() + ")");
        ////                command.add("-border");
        ////                command.add("" + targetWidth / 2);
        ////                command.add("-gravity");
        ////                command.add("center");
        ////                command.add("-crop");
        ////                command.add(targetWidth + "x" + targetHeight + "+0+0");
        ////                command.add("+repage");
        //            }
        //
        //            // Set qualtity
        //            command.add("-quality");
        //            command.add("" + quality);
        //
        //            // Set filenames
        //            command.add(original.getAbsolutePath());
        //            command.add(destination.getAbsolutePath());
        //
        //            // Execute command
        //            execCommand(command.toArray(new String[]{}));
        //            return;
        //
        //        }
        //        catch (Exception e)
        //        {
        //            throw new OtherObjectsException("Could not resize image: " + original, e);
        //        }

        FileInputStream in = null;
        FileInputStream out = null;
        try
        {
            // read in the original image from an input stream
            in = new FileInputStream(original);
            SeekableStream s = SeekableStream.wrapInputStream(in, true);
            RenderedOp image = JAI.create("stream", s);
            ((OpImage) image.getRendering()).setTileCache(null);

            // now resize the image
            float scale = targetWidth / image.getWidth();

            ParameterBlock pb = new ParameterBlock();
            pb.addSource(image); // The source image
            pb.add(scale); // The xScale
            pb.add(scale); // The yScale
            pb.add(0.0F); // The x translation
            pb.add(0.0F); // The y translation
            pb.add(new InterpolationNearest()); // The interpolation 

            // Standard quality scale
            // RenderedOp resizedImage = JAI.create("scale", pb, null);

            // High quality scale
            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            RenderedOp resizedImage = JAI.create("SubsampleAverage", image, new Double(scale), new Double(scale), qualityHints);

            JAI.create("encode", resizedImage, out, "PNG", null);
        }
        catch (FileNotFoundException e)
        {
            throw new OtherObjectsException("Could not resize image: " + original, e);
        }
        finally
        {
            try
            {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }
            catch (IOException e)
            {
                throw new OtherObjectsException("Could not resize image: " + original, e);
            }
        }

    }

}
