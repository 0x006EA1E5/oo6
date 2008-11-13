package org.otherobjects.cms.model;

import java.awt.Dimension;

import org.otherobjects.cms.jcr.GenericJcrDaoJackrabbit;
import org.otherobjects.cms.tools.CmsImageTool;
import org.otherobjects.cms.util.ImageUtils;
/**
 * FIXME Allow different naming strategies. IE don't always append number.
 * 
 * @author rich
 *
 */
public class CmsImageDaoImpl extends GenericJcrDaoJackrabbit<CmsImage> implements CmsImageDao
{
    public CmsImageDaoImpl()
    {
        super(CmsImage.class);
    }

    private final DataFileDao dataFileDao = new DataFileDaoFileSystem();
    private final CmsImageTool cmsImageTool = new CmsImageTool();

    @Override
    public CmsImage save(CmsImage o, boolean validate)
    {
        // FIXME We need to work out which method should be overridden. Should validate, save then create images. And roll all of it back on any failure.
        CmsImage image = o;
        if (image.getNewFile() != null)
        {
            Dimension imageDimensions = ImageUtils.getImageDimensions(image.getNewFile());
            image.setOriginalWidth(new Double(imageDimensions.getWidth()).longValue());
            image.setOriginalHeight(new Double(imageDimensions.getHeight()).longValue());
            DataFile original = new DataFile(image.getNewFile());
            original.setFileName(image.getCode());
            original.setCollection(CmsImage.DATA_FILE_COLLECTION_NAME);
            original.setPath(CmsImage.ORIGINALS_PATH);
            original = this.dataFileDao.save(original);
            image.setOriginalFileName(original.getId());
            image.setNewFile(null);

            // Create thumbnail
            this.cmsImageTool.getThumbnail(image);
        }
        return super.save(image, validate);
    }

    @Override
    public void publish(CmsImage dynaNode, String message)
    {
        super.publish(dynaNode, message);
    }
    

    //    @SuppressWarnings("unchecked")
    //    private ModelAndView generateImageServiceData(HttpServletRequest request) throws IOException, SAXException, FlickrException
    //    {
    //        //String path = request.getPathInfo();
    //
    //        FlickrImageService flickr = new FlickrImageService();
    //        List results = new ArrayList();
    //        for (Photo p : flickr.getImages())
    //        {
    //            results.add(convertToCmsImage(p));
    //        }
    //
    //        ModelAndView view = new ModelAndView("jsonView");
    //        view.addObject(JsonView.JSON_DATA_KEY, results);
    //        view.addObject(JsonView.JSON_DEEP_SERIALIZE, true);
    //        return view;
    //    }
    //
    //    private CmsImage convertToCmsImage(Photo photo)
    //    {
    //        //        CmsImage image = ((CmsImageDao) this.daoService.getDao(CmsImage.class)).createCmsImage();
    //        CmsImage image = new CmsImage();//cmsImageDao.createCmsImage();
    //        image.setLabel(photo.getTitle());
    //        //image.setKeywords(photo.getTags());
    //        image.setOriginalWidth(1L);
    //        image.setOriginalHeight(1L);
    //        image.setThumbnailPath(photo.getSmallSquareUrl());
    //        image.setId(photo.getId());
    //        image.setOriginalProvider("FLICKR");
    //        return image;
    //    }
}
