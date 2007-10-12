package org.otherobjects.cms.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.model.CmsImageDao;
import org.otherobjects.cms.tools.CmsImageTool;
import org.otherobjects.cms.util.ImageUtils;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.iptc.IptcDirectory;

/**
 * FIXME Make this extendable.
 * 
 * See API documentation is available at <a href="http://www.xmlrpc.com/metaWeblogApi">http://www.xmlrpc.com/metaWeblogApi</a> 
 * @author rich
 */
@SuppressWarnings("unchecked")
public class MetaWeblogApiHandler
{
	private final Log logger = LogFactory.getLog(getClass());
	
    private UniversalJcrDao universalJcrDao;
    private CmsImageDao cmsImageDao;

    public List<Map<String, Object>> getRecentPosts(String blogid, String username, String password, int numberOfPosts)
    {
        List<BaseNode> news = universalJcrDao.getAllByJcrExpression("/jcr:root/site/news/* [@ooType='com.maureenmichaelson.site.model.NewsStory']");
        List<Map<String, Object>> posts = new ArrayList<Map<String, Object>>();

        for (BaseNode newsStory : news)
        {
            posts.add(convertNodeToPost(newsStory));
        }

        return posts;
    }

    private Map<String, Object> convertNodeToPost(BaseNode node)
    {
        Map<String, Object> post = new HashMap<String, Object>();
        post.put("postid", node.getJcrPath());
        post.put("title", node.getLabel());
        post.put("description", node.get("content") != null ? node.get("content") : "");
        post.put("link", node.getJcrPath());
        return post;
    }

    private BaseNode convertPostToNode(Map<String, Object> post)
    {
        BaseNode node = null;
        if (post.containsKey("link"))
            node = universalJcrDao.getByPath((String) post.get("link"));
        else
        {
            // FIXME Make this extendable
            node = null;//universalJcrDao.create("com.maureenmichaelson.site.model.NewsStory");
            node.setPath("/site/news/");
        }
        node.setLabel((String) post.get("title"));
        node.set("content", post.get("description"));
        node.set("publicationDate", new Date());
        return node;
    }

    public String newPost(String blogid, String username, String password, Map post, boolean publish)
    {
        BaseNode node = convertPostToNode(post);
        universalJcrDao.save(node);
        if (publish)
            universalJcrDao.publish(node, null);
        return node.getJcrPath();
    }

    public boolean editPost(String postId, String username, String password, Map post, boolean publish)
    {
        BaseNode node = convertPostToNode(post);
        universalJcrDao.save(node);
        if (publish)
            universalJcrDao.publish(node, null);
        return true;
    }

    public Object getPost(String postId, String username, String password)
    {
        BaseNode node = universalJcrDao.getByPath(postId);
        return convertNodeToPost(node);
    }
    
    /**
     * //FIXME this needs to be genericized once we have the means to handle other media files as well (flash, video, etc.)
     * 
     * @param blogid
     * @param username
     * @param password
     * @param struct
     * @return
     */
    public Object newMediaObject(String blogid, String username, String password, Map struct)
    {
    	// post should have the elements name, type and bits where name is just a label, type is the mime-type of the object and bits is the base64 encoded content
    	String type = (String) struct.get("type");
    	String name = (String) struct.get("name");
    	
    	if(type == null || !type.equalsIgnoreCase("image/jpeg")) //FIXME we need a more generic way to also be able to deal with other media types
    		return null;
    	
    	OutputStream out = null;
    	File newFile = null;
    	Map<String, String> returnStruct = new HashMap<String,String>();
    	try {
			// create temp file
			newFile = File.createTempFile("image", "jpeg");
			
			// open file for writing
			out = new BufferedOutputStream(new FileOutputStream(newFile));
			
			// copy posted content to temp file
			byte[] content = (byte[]) struct.get("bits");
			
			IOUtils.write(content, out);
			
			CmsImage cmsImage = new CmsImage();//cmsImageDao.createCmsImage();
	        cmsImage.setPath("/libraries/images/");
	        cmsImage.setCode(name.replaceAll("/", "")); //our name mustn't contain slashes but MarsEdits calls do
	        cmsImage.setLabel(name.replaceAll("/", ""));
	        
	        //now check whether an image with the same path already exists and fail if that is the case.
	        if(cmsImageDao.existsAtPath(cmsImage.getJcrPath()))
	        	throw new OtherObjectsException("Couldn't create media object as an object with the same name already exists");
	        
	        
	        cmsImage.setNewFile(newFile);

	        // Look for IPTC tags to read
	        Metadata imageMetadata = ImageUtils.getImageMetadata(newFile);
	        if (imageMetadata.containsDirectory(IptcDirectory.class))
	        {
	            Directory iptc = imageMetadata.getDirectory(IptcDirectory.class);
	            if (iptc.containsTag(IptcDirectory.TAG_OBJECT_NAME))
	                cmsImage.setLabel(iptc.getString(IptcDirectory.TAG_OBJECT_NAME));
	            if (iptc.containsTag(IptcDirectory.TAG_CAPTION))
	                cmsImage.setDescription(iptc.getString(IptcDirectory.TAG_CAPTION));
	            if (iptc.containsTag(IptcDirectory.TAG_COPYRIGHT_NOTICE))
	                cmsImage.setCopyright(iptc.getString(IptcDirectory.TAG_COPYRIGHT_NOTICE));
	            if (iptc.containsTag(IptcDirectory.TAG_KEYWORDS))
	                cmsImage.setKeywords(iptc.getString(IptcDirectory.TAG_KEYWORDS));
	        }

	        // Get file proprerties
	        cmsImageDao.save(cmsImage);
	        
	        CmsImageTool cmsImageTool = new CmsImageTool();
	        
	        returnStruct.put("url", cmsImageTool.getOriginal(cmsImage).getDataFile().getExternalUrl());
	        
    	} catch (IOException e) {
			logger.error("newMediaObject failed as the xml-rpc wasn't valid or couldn't be read", e);
			throw new OtherObjectsException("newMediaObject() failed.", e);
		}
		finally
		{
			if(out != null)
			{
				try {
					out.close();
				} catch (Exception e) {
					//noop
				}
			}	
			if(newFile != null)
			{
				try {
					newFile.delete();
				} catch (Exception e) {
					//noop
				}
			}
		}
		return returnStruct;
    	
    }

    public Object[] getCategories(String blogid, String username, String password)
    {
        return null;
    }

    public void setCmsImageDao(CmsImageDao cmsImageDao)
    {
        this.cmsImageDao = cmsImageDao;
    }
}
