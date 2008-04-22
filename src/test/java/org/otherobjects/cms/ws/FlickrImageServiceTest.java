//package org.otherobjects.cms.ws;
//
//import java.io.IOException;
//import java.util.List;
//
//import junit.framework.TestCase;
//
//import org.xml.sax.SAXException;
//
//import com.aetrion.flickr.FlickrException;
//import com.aetrion.flickr.photos.Photo;
//
//public class FlickrImageServiceTest extends TestCase
//{
//
//    public void testGetImages() throws IOException, SAXException, FlickrException
//    {
//        FlickrImageService flickr = new FlickrImageService();
//        List<Photo> images = flickr.getImages();
//        assertNotNull(images);
//        for (Photo p : images)
//        {
//            System.err.println(p.getLargeUrl());
//        }
//    }
//
//}
