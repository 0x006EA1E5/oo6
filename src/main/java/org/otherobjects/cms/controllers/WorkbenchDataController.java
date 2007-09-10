package org.otherobjects.cms.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.dao.GenericDao;
import org.otherobjects.cms.dao.PagedResult;
import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.model.CmsImageDao;
import org.otherobjects.cms.model.CompositeDatabaseId;
import org.otherobjects.cms.model.DbFolder;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.model.Folder;
import org.otherobjects.cms.model.SiteFolder;
import org.otherobjects.cms.model.SmartFolder;
import org.otherobjects.cms.types.JcrTypeServiceImpl;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.IdentifierUtils;
import org.otherobjects.cms.views.JsonView;
import org.otherobjects.cms.ws.FlickrImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.xml.sax.SAXException;

import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.photos.Photo;

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
@SuppressWarnings("unchecked")
public class WorkbenchDataController implements Controller
{
    public static final int ITEMS_PER_PAGE = 25;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private DynaNodeDao dynaNodeDao;
    private TypeService typeService;
    private DaoService daoService;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String path = request.getPathInfo();
        path = path.substring(6);

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
        else if (path.contains("/image-services/"))
            return generateImageServiceData(request);
        else
            return null;
    }

    private ModelAndView generateImageServiceData(HttpServletRequest request) throws IOException, SAXException, FlickrException
    {
        //String path = request.getPathInfo();

        FlickrImageService flickr = new FlickrImageService();
        List results = new ArrayList();
        for (Photo p : flickr.getImages())
        {
            results.add(convertToCmsImage(p));
        }

        ModelAndView view = new ModelAndView("jsonView");
        view.addObject(JsonView.JSON_DATA_KEY, results);
        view.addObject(JsonView.JSON_DEEP_SERIALIZE, true);
        return view;
    }

    private CmsImage convertToCmsImage(Photo photo)
    {
        CmsImage image = ((CmsImageDao) this.daoService.getDao(CmsImage.class)).createCmsImage();
        image.setLabel(photo.getTitle());
        //image.setKeywords(photo.getTags());
        image.setOriginalWidth(1L);
        image.setOriginalHeight(1L);
        image.setThumbnailPath(photo.getSmallSquareUrl());
        image.setId(photo.getId());
        image.setOriginalProvider("FLICKR");
        return image;
    }

    private ModelAndView generateItemData(HttpServletRequest request)
    {
        String path = request.getPathInfo();
        String id = path.substring(path.lastIndexOf("/") + 1);

        this.logger.info("Sending item data: {} ", id);
        Object item = null;
        if (IdentifierUtils.isUUID(id))
        {
            item = this.dynaNodeDao.get(id);
        }
        else
        {
            CompositeDatabaseId compositeDatabaseId = IdentifierUtils.getCompositeIdPart(id);
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

        this.logger.info("Sending select data of type: {} ", typeName);

        List allByType = null;
        // FIXME Temp hack
        if (typeName.equals("org.otherobjects.cms.types.TypeDef"))
        {
            allByType = ((JcrTypeServiceImpl) this.typeService).getTypeDefDao().getAll();
        }
        else if (typeName.equals("org.otherobjects.cms.model.CmsImage"))
        {
            allByType = this.dynaNodeDao.getAllByJcrExpression("/jcr:root//element(*, oo:node) [@ooType = 'org.otherobjects.cms.model.CmsImage'] order by @modificationTimestamp descending");
        }
        else
        {
            allByType = this.dynaNodeDao.getAllByType(typeName);
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
            DynaNode node = this.dynaNodeDao.get(nodeId);
            jcrPath = node.getJcrPath();
        }

        // FIXME M2 Use NavigatorService
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
        List<DynaNode> contents = this.dynaNodeDao.getAllByPath(jcrPath);

        for (DynaNode dynaNode : contents)
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
    private ModelAndView generateListingData(HttpServletRequest request)
    {
        String jcrPath = "/site";
        DynaNode node = null;

        ModelAndView view = new ModelAndView("jsonView");

        String q = request.getParameter("q");
        String sort = request.getParameter("sort");

        String nodeId = request.getParameter("node");

        if (nodeId != null && nodeId.length() > 10)
        {
            node = this.dynaNodeDao.get(nodeId);
            jcrPath = node.getJcrPath();
        }
        else
        {
            node = this.dynaNodeDao.getByPath(jcrPath);
        }

        // FIXME M3 Can we exclude folders in the query?
        boolean asc = false;
        String dir = request.getParameter("dir");
        if (dir == null || dir.equals("ASC"))
            asc = true;

        PagedResult pagedResult = null;
        if (node instanceof SiteFolder)
        {
            //pagedResult = this.dynaNodeDao.getPagedByPath(jcrPath, ITEMS_PER_PAGE, getRequestedPage(request), q, sort, asc);
            String query = "/jcr:root" + jcrPath + "/* [not(jcr:like(@ooType,'%Folder')) and not(jcr:like(@ooType,'%MetaData'))] order by @modificationTimestamp descending";
            pagedResult = this.dynaNodeDao.pageByJcrExpression(query, ITEMS_PER_PAGE, getRequestedPage(request));
        }
        else if (node instanceof SmartFolder)
        {
            SmartFolder dbFolder = (SmartFolder) node;
            String query = dbFolder.getQuery();

            query = "/jcr:root/site//*[jcr:contains(., '" + query + "') and not(jcr:like(@ooType,'%Folder'))] order by @modificationTimestamp descending";

            Assert.hasText(query, "SmartFolder must have a query.");
            pagedResult = this.dynaNodeDao.pageByJcrExpression(query, ITEMS_PER_PAGE, getRequestedPage(request));

        }
        else if (node instanceof DbFolder)
        {
            DbFolder dbFolder = (DbFolder) node;
            String dbType = dbFolder.getMainType();
            String dbQuery = dbFolder.getMainTypeQuery();

            GenericDao genericDao = this.daoService.getDao(dbType);
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

    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)
    {
        this.dynaNodeDao = dynaNodeDao;
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }
}
