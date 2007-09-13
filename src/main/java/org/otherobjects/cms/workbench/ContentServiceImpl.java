package org.otherobjects.cms.workbench;

import java.io.File;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.model.CmsImageDao;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.ws.FlickrImageService;
import org.springframework.util.Assert;

import com.aetrion.flickr.photos.Photo;

/**
 * Default implementaion of content service.
 * 
 * @author rich
 */
public class ContentServiceImpl implements ContentService
{
    private DaoService daoService;
    private DynaNodeDao dynaNodeDao;

    public DynaNode createImage(String service, String imageId)
    {
        try
        {
            Assert.hasText("provider must be specified.", service);
            Assert.hasText("imageId must be specified.", imageId);

            FlickrImageService flickr = new FlickrImageService();
            CmsImageDao cmsImageDao = ((CmsImageDao) this.daoService.getDao(CmsImage.class));

            Photo photo = flickr.getImage(imageId);

            // FIXME Sort out medium/large url problems
            // FIXME Sort out unique temp path
            File tmpFile = new File("/tmp/flickr.jpg");
            System.err.println(photo.getMediumUrl());
            FileUtils.copyURLToFile(new URL(photo.getMediumUrl()), tmpFile);
            CmsImage image = cmsImageDao.createCmsImage();
            image.setPath("/libraries/images/");
            image.setCode("" + new Date().getTime());
            //        image.setCode(StringUtils.substringAfterLast(photo.getSmallUrl(), "/"));
            image.setNewFile(tmpFile);
            image.setLabel(photo.getTitle());
            //image.setKeywords(photo.getTags());
            image.setOriginalId(photo.getId());
            image.setOriginalProvider("FLICKR");
            image = (CmsImage) cmsImageDao.save(image);
            cmsImageDao.publish(image);
            return image;
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not create image: " + service + " " + imageId, e);
        }

    }

    public DynaNode createItem(String container, String typeName)
    {
        Assert.hasText("container must be specified.", container);
        Assert.hasText("typeName must be specified.", typeName);

        //TODO Make sure this throws exception is not exists
        DynaNode parent = this.dynaNodeDao.get(container);
        this.dynaNodeDao.create(typeName);

        //TODO M2 Merge this code with NavService version
        int c = 0;
        do
        {
            String newPath = parent.getJcrPath() + "/untitled-" + ++c + ".html";
            boolean alreadyExists = (this.dynaNodeDao.existsAtPath(newPath));
            if (!alreadyExists)
                break;

        }
        while (true);

        DynaNode newNode = this.dynaNodeDao.create(typeName);
        newNode.setPath(parent.getJcrPath());
        newNode.setCode("untitled-" + c + ".html"); //TODO M2 Auto generate
        newNode.setLabel("Untitled " + c);
        return this.dynaNodeDao.save(newNode);
    }

    public DynaNode publishItem(String uuid, String message)
    {
        Assert.hasText("item must be specified.", uuid);

        DynaNode item = this.dynaNodeDao.get(uuid);
        this.dynaNodeDao.publish(item, message);
        return item;
    }

    public DynaNode restoreItemVersion(String uuid, int changeNumber, String message)
    {
        Assert.hasText("item must be specified.", uuid);

        DynaNode item = this.dynaNodeDao.get(uuid);
        this.dynaNodeDao.restoreVersionByChangeNumber(item, changeNumber, true);
        return item;
    }

    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)
    {
        this.dynaNodeDao = dynaNodeDao;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

}
