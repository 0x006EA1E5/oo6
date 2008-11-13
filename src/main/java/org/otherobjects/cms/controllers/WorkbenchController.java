package org.otherobjects.cms.controllers;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.Url;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.GenericDao;
import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.DbFolder;
import org.otherobjects.cms.model.Folder;
import org.otherobjects.cms.model.FolderDao;
import org.otherobjects.cms.model.Role;
import org.otherobjects.cms.model.SiteFolder;
import org.otherobjects.cms.model.SmartFolder;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.ActionUtils;
import org.otherobjects.cms.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Main workbench controller. Handles critical list/view/edit functions.
 * 
 * <p>Surretly only supports SiteFolders, DbFolders and SmartFolders.
 * 
 * @author rich
 */
@Controller
public class WorkbenchController
{
    private static final int ITEMS_PER_PAGE = 50;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private TypeService typeService;

    @Resource
    private DaoService daoService;

    //    @Resource
    //    private LocaleResolver localeResolver;

    @RequestMapping({"", "/", "/workbench/*"})
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        //        String newLocale = ServletRequestUtils.getStringParameter(request, "locale");
        //        if (newLocale != null)
        //        {
        //            this.localeResolver.setLocale(request, response, StringUtils.parseLocaleString(newLocale));
        //            this.logger.info("Locale set to: " + this.localeResolver.resolveLocale(request));
        //        }

        ModelAndView mav = new ModelAndView("/otherobjects/templates/legacy/pages/overview");
        return mav;
    }

    @RequestMapping({"/workbench/view/*"})
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String id = RequestUtils.getId(request);
        ModelAndView mav = new ModelAndView("/otherobjects/templates/legacy/pages/view");
        mav.addObject("id", id);
        return mav;
    }

    @RequestMapping({"/workbench/edit/*"})
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String id = RequestUtils.getId(request);
        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
        ModelAndView mav = new ModelAndView("/otherobjects/templates/legacy/pages/edit");
        BaseNode item = universalJcrDao.get(id);
        mav.addObject("id", id);
        mav.addObject("object", item);
        mav.addObject("typeDef", item.getTypeDef());
        return mav;
    }

    @RequestMapping({"/workbench/create/*"})
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String type = RequestUtils.getId(request);
        TypeDef typeDef = typeService.getType(type);
        ModelAndView mav = new ModelAndView("/otherobjects/templates/legacy/pages/edit");

        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
        BaseNode create = universalJcrDao.create(type);
        if (request.getParameter("code") != null)
            create.setCode(request.getParameter("code"));
        mav.addObject("object", new Role());
        mav.addObject("containerId", request.getParameter("container"));
        mav.addObject("typeDef", typeDef);
        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping({"/workbench/list/*"})
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String id = RequestUtils.getId(request);
        int requestedPage = RequestUtils.getInt(request, "page", 1);
        String q = null; // TODO 

        FolderDao folderDao = (FolderDao) this.daoService.getDao(Folder.class);
        Folder folder = folderDao.get(id);
        ModelAndView mav = new ModelAndView("/otherobjects/templates/legacy/pages/list");
        mav.addObject("id", id);
        mav.addObject("folder", folder);

        if (folder instanceof SiteFolder)
        {
            UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
            mav.addObject("items", universalJcrDao.getAllByPath(((SiteFolder) folder).getJcrPath()));
        }
        else if (folder instanceof DbFolder)
        {
            DbFolder dbFolder = (DbFolder) folder;
            String dbType = dbFolder.getMainType();
            String dbQuery = dbFolder.getMainTypeQuery();

            GenericDao genericDao = this.daoService.getDao(dbType);

            Assert.notNull(genericDao, "No DAO found for: " + dbType);
            Assert.isTrue(!(genericDao instanceof GenericJcrDao), "Dao must be defined for this database object.");

            String sort = null;
            boolean asc = true;
            if (StringUtils.isNotBlank(dbQuery))
                mav.addObject("items", genericDao.getPagedByQuery(dbQuery, ITEMS_PER_PAGE, requestedPage, q, sort, asc).getItems());
            else
                mav.addObject("items", genericDao.getAllPaged(ITEMS_PER_PAGE, requestedPage, q, sort, asc).getItems());
        }
        else if (folder instanceof SmartFolder)
        {
//            SmartFolder smartFolder = (SmartFolder) node;
//            String query = smartFolder.getSearchTerm();
//            String jcr = smartFolder.getQuery();
//            
//            if (StringUtils.isNotEmpty(jcr))
//                query = jcr;
//            else
//                query = "/jcr:root/site//*[jcr:contains(., '" + query + "') and not(jcr:like(@ooType,'%Folder'))] order by @modificationTimestamp descending";
//            
//            Assert.hasText(query, "SmartFolder must have a query.");
//            pagedResult = this.universalJcrDao.pageByJcrExpression(query, ITEMS_PER_PAGE, getRequestedPage(request));
            
        }
        else
            throw new OtherObjectsException("Currently only listings for SiteFolder, DbFolder and SmartFolder are supported.");

        return mav;
    }

    @RequestMapping({"/workbench/search*"})
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Search JCR
        String q = request.getParameter("q");
        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
        List<BaseNode> results = universalJcrDao.getAllByJcrExpression("/jcr:root/site//(*) [jcr:contains(data/., '" + q + "')]");

        // Search DB
        ModelAndView mav = new ModelAndView("/otherobjects/templates/legacy/pages/search");
        mav.addObject("results", results);
        return mav;
    }

    @RequestMapping({"/workbench/history/*"})
    public ModelAndView history(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        return null;
    }

    /* Actions */

    @RequestMapping({"/workbench/publish/*"})
    public ModelAndView publish(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Publish
        String id = RequestUtils.getId(request);
        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
        BaseNode item = universalJcrDao.get(id);
        universalJcrDao.publish(item, null);

        // FIXME - Catch errors

        // Response
        ActionUtils actionUtils = new ActionUtils(request, response, null, null);
        actionUtils.flashInfo("Your object was published.");
        Url u = new Url("/otherobjects/workbench/view/" + item.getId());
        response.sendRedirect(u.toString());
        return null;
    }

    @RequestMapping({"/workbench/unpublish/*"})
    public ModelAndView unpublish(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        return null;
    }

    @RequestMapping({"/workbench/delete/*"})
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // FIXME Need to publish this delete too.
        String id = RequestUtils.getId(request);
        FolderDao folderDao = (FolderDao) this.daoService.getDao(Folder.class);
        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);

        BaseNode item = universalJcrDao.get(id);
        universalJcrDao.remove(id);

        ActionUtils actionUtils = new ActionUtils(request, response, null, null);
        actionUtils.flashInfo("Your object was deleted.");

        SiteFolder folder = folderDao.getByPath(item.getPath());
        Url u = new Url("/otherobjects/workbench/list/" + folder.getId());
        response.sendRedirect(u.toString());
        return null;
    }

    @RequestMapping({"/workbench/revert/*"})
    public ModelAndView revert(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        return null;
    }

    @RequestMapping({"/workbench/reorder/*"})
    public ModelAndView reorder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        return null;
    }

}
