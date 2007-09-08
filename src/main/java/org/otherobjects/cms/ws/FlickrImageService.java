package org.otherobjects.cms.ws;

import java.io.IOException;
import java.util.List;

import org.otherobjects.cms.model.DynaNode;
import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;

public class FlickrImageService
{
    private static final String API_KEY = "46e1080c218c61290171fb035d87558e";

    public List getImages() throws IOException, SAXException, FlickrException
    {
        String apiKey = API_KEY;
        Flickr f = new Flickr(apiKey);
        PhotosInterface photosInterface = f.getPhotosInterface();
        PeopleInterface peopleInterface = f.getPeopleInterface();

        SearchParameters params = new SearchParameters();
        params.setUserId(peopleInterface.findByUsername("raston").getId());
        params.setSort(SearchParameters.DATE_POSTED_DESC);
        return photosInterface.search(params, 50, 1);
    }

    public DynaNode getImage(String key) throws IOException, SAXException, FlickrException
    {
        return null;
        //        String apiKey = "46e1080c218c61290171fb035d87558e";
        //        Flickr f = new Flickr(apiKey);
        //        PhotosInterface photosInterface = f.getPhotosInterface();
        //        
        //        Collection results = photosInterface.echo(Collections.EMPTY_LIST);
    }
}
