package org.otherobjects.cms.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.model.CmsImageDao;
import org.otherobjects.cms.util.ImageUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.iptc.IptcDirectory;

public class UploadController implements Controller
{
    private final Log logger = LogFactory.getLog(UploadController.class);

    private DaoService daoService;

    @SuppressWarnings("unchecked")
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        MultipartHttpServletRequest uploadRequest = (MultipartHttpServletRequest) request;
        Iterator fileNames = uploadRequest.getFileNames();
        while (fileNames.hasNext())
        {
            processFile(uploadRequest.getFile((String) fileNames.next()));
        }
        return null;
    }

    private void processFile(MultipartFile file) throws OtherObjectsException, IOException
    {
        this.logger.info("Received file " + file.getOriginalFilename());

        CmsImageDao cmsImageDao = (CmsImageDao) this.daoService.getDao(CmsImage.class);

        File newFile = new File("/tmp/" + file.getOriginalFilename());
        newFile.createNewFile();
        file.transferTo(newFile);
        CmsImage cmsImage = new CmsImage(); //cmsImageDao.createCmsImage();
        cmsImage.setPath("/libraries/images/");
        cmsImage.setCode(file.getOriginalFilename());
        cmsImage.setLabel(file.getOriginalFilename());
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
        newFile.delete();
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }
}
