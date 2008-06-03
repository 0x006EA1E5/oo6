package org.otherobjects.cms.controllers;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.types.TypeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;



@Controller
public class TestController extends MultiActionController
{
    @Resource
    private DaoService daoService;

    @Resource
    private TypeService typeService;

    @SuppressWarnings("unchecked")
    public ModelAndView test(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        System.err.println("TEST");
        
        // Load data file
        logger.debug("Running setup scripts.");
        Binding binding = new Binding();
        binding.setProperty("daoService", daoService);
        binding.setProperty("typeService", typeService);
        GroovyShell shell = new GroovyShell(binding);
        String script = IOUtils.toString(new FileInputStream("data-transfer/import.groovy"));
        shell.evaluate(script);
        
        
//        MultipartHttpServletRequest uploadRequest = (MultipartHttpServletRequest) request;
//        Iterator fileNames = uploadRequest.getFileNames();
//        while (fileNames.hasNext())
//        {
//            processFile(uploadRequest.getFile((String) fileNames.next()));
//        }
        return null;
    }
//
//    private void processFile(MultipartFile file) throws OtherObjectsException, IOException
//    {
//        this.logger.info("Received file " + file.getOriginalFilename());
//
//        CmsImageDao cmsImageDao = (CmsImageDao) this.daoService.getDao(CmsImage.class);
//
//        File newFile = new File("/tmp/" + file.getOriginalFilename());
//        newFile.createNewFile();
//        file.transferTo(newFile);
//        CmsImage cmsImage = new CmsImage(); //cmsImageDao.createCmsImage();
//        cmsImage.setPath("/libraries/images/");
//        cmsImage.setCode(file.getOriginalFilename());
//        cmsImage.setLabel(file.getOriginalFilename());
//        cmsImage.setNewFile(newFile);
//
//        // Look for IPTC tags to read
//        Metadata imageMetadata = ImageUtils.getImageMetadata(newFile);
//        if (imageMetadata.containsDirectory(IptcDirectory.class))
//        {
//            Directory iptc = imageMetadata.getDirectory(IptcDirectory.class);
//            if (iptc.containsTag(IptcDirectory.TAG_OBJECT_NAME))
//                cmsImage.setLabel(iptc.getString(IptcDirectory.TAG_OBJECT_NAME));
//            if (iptc.containsTag(IptcDirectory.TAG_CAPTION))
//                cmsImage.setDescription(iptc.getString(IptcDirectory.TAG_CAPTION));
//            if (iptc.containsTag(IptcDirectory.TAG_COPYRIGHT_NOTICE))
//                cmsImage.setCopyright(iptc.getString(IptcDirectory.TAG_COPYRIGHT_NOTICE));
//            if (iptc.containsTag(IptcDirectory.TAG_KEYWORDS))
//                cmsImage.setKeywords(iptc.getString(IptcDirectory.TAG_KEYWORDS));
//        }
//
//        // Get file proprerties
//        cmsImageDao.save(cmsImage);
//        newFile.delete();
//    }
//
//    public void setDaoService(DaoService daoService)
//    {
//        this.daoService = daoService;
//    }
}
