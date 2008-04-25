package org.otherobjects.cms.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.GenericDao;
import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.dao.PagedList;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CompositeDatabaseId;
import org.otherobjects.cms.model.DbFolder;
import org.otherobjects.cms.model.Folder;
import org.otherobjects.cms.model.SiteFolder;
import org.otherobjects.cms.model.SmartFolder;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.IdentifierUtils;
import org.otherobjects.cms.views.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller providing a REST style interface to site data. Currently supports navigator structure and
 * container contents. Data is served in JSON format.
 * 
 * <p>/navigator?node= (sends sub-contaniners of specified container)
 * <br>/listing?node= (sends items in specified container)
 * <br/>/type/typeName (sends typeDef info)
 * 
 * <p>FIXME Turn this into subclasses
 * 
 * @author rich
 */
@Controller
public class DataController
{
    public static final int ITEMS_PER_PAGE = 25;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private UniversalJcrDao universalJcrDao;

    @Resource
    private TypeService typeService;
    
    //@Resource
    private DaoService daoService;

    @RequestMapping("/data/**")
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String path = request.getPathInfo();
        path = path.substring(5);

        if (path.contains("/types"))
            return generateTypeData(request);
        else if (path.contains("/item/"))
            return generateItemData(request);
        else if (path.endsWith("/listing"))
            return generateListingData(request);
        else if (path.contains("/select"))
            return generateSelectData(request);
        else if (path.endsWith("/navigator"))
            return generateNavigatorData(request);
        //        else if (path.contains("/image-services/"))
        //            return generateImageServiceData(request);
        else
            return null;
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

    @SuppressWarnings("unchecked")
    private ModelAndView generateItemData(HttpServletRequest request)
    {
        String path = request.getPathInfo();
        String id = path.substring(path.lastIndexOf("/") + 1);

        this.logger.info("Sending item data: {} ", id);
        Object item = null;
        if (IdentifierUtils.isUUID(id))
        {
            item = this.universalJcrDao.get(id);
        }
        else
        {
            CompositeDatabaseId compositeDatabaseId = IdentifierUtils.getCompositeDatabaseId(id);
            if (compositeDatabaseId != null)
            {
                GenericDao genericDao = this.daoService.getDao(compositeDatabaseId.getClazz());
                item = genericDao.get(compositeDatabaseId.getId());
                try
                {
                    //FIXME Can we put this somewhere else?
                    PropertyUtils.setSimpleProperty(item, "typeDef", this.typeService.getType(item.getClass().getName()));
                }
                catch (Exception e)
                {
                    item = null; // an item without a typeDef is useless so nullify it
                }
            }
        }

        Assert.notNull(item, "No item found: " + id);

        ModelAndView view = new ModelAndView("jsonView");
        view.addObject(JsonView.JSON_DATA_KEY, item);
        view.addObject(JsonView.JSON_DEEP_SERIALIZE, true);
        return view;
    }

    @SuppressWarnings("unchecked")
    private ModelAndView generateSelectData(HttpServletRequest request)
    {
        String path = request.getPathInfo();
        String typeName = path.substring(path.lastIndexOf("/") + 1);
        Class typeClass = null;
        try
        {
            typeClass = Class.forName(typeName);
        }
        catch (ClassNotFoundException e)
        {
        }

        Assert.notNull(typeClass, "Can only generate data for existing classes");

        this.logger.info("Sending select data of type: {} ", typeName);

        List allByType = null;
        // FIXME Temp hack
        if (typeName.equals("org.otherobjects.cms.types.TypeDef"))
        {
            allByType = daoService.getDao(TypeDef.class).getAll();
        }
        else if (typeName.equals("org.otherobjects.cms.model.CmsImage"))
        {
            allByType = this.universalJcrDao.getAllByJcrExpression("/jcr:root//element(*, oo:node) [@ooType = 'org.otherobjects.cms.model.CmsImage'] order by @modificationTimestamp descending");
        }
        // FIXME Must be a better way of testing for specfic daos
        //        else if (! (daoService.getDao(typeName) instanceof BaseNodeDao))
        //        {
        //        	allByType = daoService.getDao(typeName).getAll();
        //        }
        else if (BaseNode.class.isAssignableFrom(typeClass))
        {
            UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao(BaseNode.class);
            allByType = dao.getAllByType(typeClass);
        }
        else
        {
            GenericDao dao = daoService.getDao(typeName);
            allByType = dao.getAll();
        }

        ModelAndView view = new ModelAndView("jsonView");
        view.addObject(JsonView.JSON_DATA_KEY, allByType);
        view.addObject(JsonView.JSON_DEEP_SERIALIZE, false);
        return view;
    }

    private ModelAndView generateTypeData(HttpServletRequest request)
    {
        String path = request.getPathInfo();
        String typeName = path.substring(path.lastIndexOf("/") + 1);

        this.logger.info("Sending type definition: {} ", typeName);

        TypeDef type = this.typeService.getType(typeName);
        Assert.notNull(type, "No type found: " + typeName);

        ModelAndView view = new ModelAndView("jsonView");
        view.addObject(JsonView.JSON_DATA_KEY, type);
        //        view.addObject(JsonView.JSON_INCLUDES_KEY, "properties");
        view.addObject(JsonView.JSON_DEEP_SERIALIZE, true);
        return view;
    }

    private ModelAndView generateNavigatorData(HttpServletRequest request)
    {
        ModelAndView view = new ModelAndView("jsonView");

        String nodeId = request.getParameter("node");
        String jcrPath = "/";
        if (nodeId != null && nodeId.length() > 10)
        {
            BaseNode node = universalJcrDao.get(nodeId);
            jcrPath = node.getJcrPath();
        }

        // FIXME M2 Use NavigatorService
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
        List<BaseNode> contents = universalJcrDao.getAllByPath(jcrPath);

        for (BaseNode dynaNode : contents)
        {
            if (dynaNode instanceof Folder)
            {
                Map<String, Object> n1 = new HashMap<String, Object>();
                n1.put("id", dynaNode.getId());
                n1.put("code", dynaNode.getCode());

                n1.put("allAllowedTypes", dynaNode.get("allAllowedTypes"));

                // Localise labels
                String label = dynaNode.getLabel();
                // TODO This has moved to JsonView -- may move back!
                //                if (label.startsWith("$"))
                //                {
                //                    label = label.substring(2, label.length() -1);
                //                    label = requestContext.getMessage(label);
                //                }
                n1.put("text", label);

                if (!StringUtils.isEmpty((String) dynaNode.get("cssClass")))
                {
                    n1.put("cls", dynaNode.get("cssClass") + "-nav-item");
                    n1.put("allowDrag", false);
                }
                else
                    n1.put("cls", "folder");
                n1.put("leaf", false);
                if (dynaNode.getCode().equals("site"))
                    n1.put("expanded", true);

                nodes.add(n1);
            }
        }
        view.addObject(JsonView.JSON_DATA_KEY, nodes);
        view.addObject(JsonView.JSON_DEEP_SERIALIZE, true);
        return view;
    }

    /**
     * Called to populate an ext grid
     * ext will always send the parameters 
     * <ul>
     * 	<li>start - offset </li>
     *  <li>limit - max items per page</li>
     * </ul>
     * It might also send 
     * <ul>
     * 	<li>dir - which is either ASC or DESC to indicate sort order</li>
     *  <li>sort - name of the colum to sort on</li>
     *  <li>q - a query string to do a full text search with</li>
     * </ul>
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    private ModelAndView generateListingData(HttpServletRequest request)
    {
        String jcrPath = "/site";
        BaseNode node = null;

        ModelAndView view = new ModelAndView("jsonView");

        String q = request.getParameter("q");
        String sort = request.getParameter("sort");

        String nodeId = request.getParameter("node");

        if (nodeId != null && nodeId.length() > 10)
        {
            node = universalJcrDao.get(nodeId);
            jcrPath = node.getJcrPath();
        }
        else
        {
            node = universalJcrDao.getByPath(jcrPath);
        }

        // FIXME M3 Can we exclude folders in the query?
        boolean asc = false;
        String dir = request.getParameter("dir");
        if (dir == null || dir.equals("ASC"))
            asc = true;

        PagedList pagedResult = null;
        if (node instanceof SiteFolder)
        {
            //pagedResult = this.universalJcrDao.getPagedByPath(jcrPath, ITEMS_PER_PAGE, getRequestedPage(request), q, sort, asc);
            String search = "";
            String sortString = "order by @modificationTimestamp descending";

            if (StringUtils.isNotEmpty(sort))
                sortString = "order by @" + sort + " " + (asc ? "" : "descending");
            if (StringUtils.isNotEmpty(q))
                search = "and jcr:contains(.,'" + q + "')";
            String query = "/jcr:root" + jcrPath + "/* [not(jcr:like(@ooType,'%Folder')) and not(jcr:like(@ooType,'%MetaData')) " + search + "] " + sortString;
            pagedResult = this.universalJcrDao.pageByJcrExpression(query, ITEMS_PER_PAGE, getRequestedPage(request));
        }
        else if (node instanceof SmartFolder)
        {
            SmartFolder smartFolder = (SmartFolder) node;
            String query = smartFolder.getSearchTerm();
            String jcr = smartFolder.getQuery();

            if (StringUtils.isNotEmpty(jcr))
                query = jcr;
            else
                query = "/jcr:root/site//*[jcr:contains(., '" + query + "') and not(jcr:like(@ooType,'%Folder'))] order by @modificationTimestamp descending";

            Assert.hasText(query, "SmartFolder must have a query.");
            pagedResult = this.universalJcrDao.pageByJcrExpression(query, ITEMS_PER_PAGE, getRequestedPage(request));

        }
        else if (node instanceof DbFolder)
        {
            DbFolder dbFolder = (DbFolder) node;
            String dbType = dbFolder.getMainType();
            String dbQuery = dbFolder.getMainTypeQuery();

            GenericDao genericDao = this.daoService.getDao(dbType);

            Assert.isTrue(!(genericDao instanceof GenericJcrDao), "Dao must be defined for this database object.");

            if (StringUtils.isNotBlank(dbQuery))
                pagedResult = genericDao.getPagedByQuery(dbQuery, ITEMS_PER_PAGE, getRequestedPage(request), q, sort, asc);
            else
                pagedResult = genericDao.getAllPaged(ITEMS_PER_PAGE, getRequestedPage(request), q, sort, asc);
        }
        else
            throw new OtherObjectsException("Currently only listings for SiteFolder or DbFolder are supporter");

        Map resultMap = new HashMap();
        resultMap.put("items", pagedResult);
        resultMap.put("totalItems", pagedResult.getItemTotal());

        view.addObject(JsonView.JSON_DATA_KEY, resultMap);
        view.addObject(JsonView.JSON_INCLUDES_KEY, new String[]{"data"});
        return view;
    }

    /**
     * ext sends to parameters to control paging: a limit and a start. Our page number
     * needs to be calculated from these. On the first request there are  
     * @param request
     * @return
     */
    private int getRequestedPage(HttpServletRequest request)
    {
        int start = Integer.parseInt(request.getParameter("start"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        return (start / limit) + 1;
    }
}