package org.otherobjects.cms.util;

import java.awt.Color;
import java.io.File;

/**
 * ImageResizers should support JPEGs, PNGs and non-animated GIFs.
 * 
 * @author <a href="mailto:rich">rich</a>
 */
public interface ImageResizer
{
    public void resize(File original, File destination, Integer targetWidth, Integer targetHeight, Color backgroundColor, Integer quality);
}
