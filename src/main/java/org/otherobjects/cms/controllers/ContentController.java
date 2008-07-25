package org.otherobjects.cms.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.views.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Default implementaion of content service.
 * 
 * FIXME Should this be based on BaseNode or BaseNode?
 * 
 * @author rich
 */
@Controller
public class ContentController
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private UniversalJcrDao universalJcrDao;

    @RequestMapping("/content/publish/**")
    public ModelAndView publishItem(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String uuid = request.getPathInfo().substring(17);
        String message = request.getParameter("message");
        
        Assert.hasText("item must be specified.", uuid);

        BaseNode item = universalJcrDao.get(uuid);
        logger.debug("Publishing item: {}", item);
        universalJcrDao.publish(item, message);
        
        ModelAndView view = new ModelAndView("jsonView");
        view.addObject(JsonView.JSON_DATA_KEY, item);
        view.addObject(JsonView.JSON_DEEP_SERIALIZE, true);
        return view;
    }
    
    public CmsImage createImage(String service, String imageId)
    {
        return null;
//        try
//        {
//            Assert.hasText("provider must be specified.", service);
//            Assert.hasText("imageId must be specified.", imageId);
//
//            FlickrImageService flickr = new FlickrImageService();
//            CmsImageDao cmsImageDao = ((CmsImageDao) this.daoService.getDao(CmsImage.class));
//
//            Photo photo = flickr.getImage(imageId);
//
//            // FIXME Sort out medium/large url problems
//            // FIXME Sort out unique temp path
//            File tmpFile = new File("/tmp/flickr.jpg");
//            System.err.println(photo.getMediumUrl());
//            FileUtils.copyURLToFile(new URL(photo.getMediumUrl()), tmpFile);
//            CmsImage image = new CmsImage();//cmsImageDao.createCmsImage();
//            image.setPath("/libraries/images/");
//            image.setCode("" + new Date().getTime());
//            //        image.setCode(StringUtils.substringAfterLast(photo.getSmallUrl(), "/"));
//            image.setNewFile(tmpFile);
//            image.setLabel(photo.getTitle());
//            //image.setKeywords(photo.getTags());
//            image.setOriginalId(photo.getId());
//            image.setOriginalProvider("FLICKR");
//            image = cmsImageDao.save(image);
//            cmsImageDao.publish(image, null);
//            return image;
//        }
//        catch (Exception e)
//        {
//            throw new OtherObjectsException("Could not create image: " + service + " " + imageId, e);
//        }

    }

//    public BaseNode createItem(String container, String typeName)
//    {
//        Assert.hasText("container must be specified.", container);
//        Assert.hasText("typeName must be specified.", typeName);
//
//        //TODO M2 Merge this code with NavService version
//        //TODO Make sure this throws exception is not exists
//        BaseNode parent = universalJcrDao.get(container);
//
//        BaseNode newNode = create(typeName);
//        newNode.setPath(parent.getJcrPath());
//        
//        int c = 0;
//        do
//        {
//            newNode.setLabel("Untitled " + (++c));
//            String newPath = newNode.getJcrPath();
//            boolean alreadyExists = (this.universalJcrDao.existsAtPath(newPath));
//            if (!alreadyExists)
//                break;
//        }
//        while (true);
//
//        return universalJcrDao.save(newNode);
//    }

//    /**
//     * FIXME Put this somewhere more generic.
//     * 
//     * Creates an object of the specified type.
//     * 
//     * @param typeName
//     * @return
//     */
//    private BaseNode create(String typeName)
//    {
//        try
//        {
//            // Should this use DynaNode
//            Object newInstance = Class.forName(typeName).newInstance();
//            if(newInstance instanceof BaseNode)
//            {
//                ((BaseNode)newInstance).setOoType(typeName);
//            }
//            return (BaseNode) newInstance;
//        }
//        catch (Exception e)
//        {
//            throw new OtherObjectsException("Could not create object of type: " + typeName,e);
//        }
//    }



    public BaseNode restoreItemVersion(String uuid, int changeNumber, String message)
    {
        Assert.hasText("item must be specified.", uuid);

        BaseNode item = universalJcrDao.get(uuid);
        universalJcrDao.restoreVersionByChangeNumber(item, changeNumber);
        return item;
    }

//    public void setDaoService(DaoService daoService)
//    {
//        this.daoService = daoService;
//    }

    public void setUniversalJcrDao(UniversalJcrDao universalJcrDao)
    {
        this.universalJcrDao = universalJcrDao;
    }

}
