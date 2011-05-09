/**
 * 
 */
package org.otherobjects.cms.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.otherobjects.cms.binding.BindService;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.GenericDao;
import org.otherobjects.cms.dao.PagedList;
import org.otherobjects.cms.datastore.HibernateDataStore;
import org.otherobjects.cms.datastore.JackrabbitDataStore;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.validation.ValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * <hr/>
 * Created 10 Jun 2010, Last Committed $Date $
 * @author geales
 * @author Last committed by $Author $
 * @version $Revision $
 */
@Controller
@RequestMapping("/node/**")
public class NodeController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private TypeService typeService;

    @Resource
    private DaoService daoService;

    @Resource
    private HibernateDataStore hibernateDataStore;

    @Resource
    private JackrabbitDataStore jackrabbitDataStore;

    @Resource
    private BindService bindService;

    @Resource
    private ValidatorService validatorService;
    
    @Resource
    private BeanFactory beanFactory;
    
    

    
    @RequestMapping(value = {"/{type}/{id}"}, method = RequestMethod.GET)
    public Model get(HttpServletRequest request, HttpServletResponse response, Model model, 
            @PathVariable TypeDef type, @PathVariable Long id) throws ServletException, IOException
    {
        logger.debug("In NodeController get {} {}", type, id);

        if(type != null && daoService.hasDao(type.getClassName()))
        {
            GenericDao<? extends Serializable, Serializable> dao = daoService.getDao(type.getClassName());
            Serializable s = id;
            Serializable o = dao.get(s);
            
            try {
                String version = BeanUtils.getProperty(o, "version");
                String etag = request.getHeader("If-None-Match");
                
                if(version.equals(etag))
                {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return null;
                }
                
                response.setHeader("Etag", version);
                
            } catch (Exception e) {}
            
            model.addAttribute("item", o);
        }
        
        return model;
        
    }

    @RequestMapping(value = {"/{type}/all"}, method = RequestMethod.GET)
    public Model getAll(HttpServletResponse response, Model model,
            @PathVariable String type) throws ServletException, IOException
    {
        logger.debug("In NodeController getAll {}", type);

        if(daoService.hasDao(type))
        {
            GenericDao<? extends Serializable, Serializable> dao = daoService.getDao(type);
            List<? extends Serializable> list = dao.getAll();
            
            model.addAttribute("item", list);
        }
        
        return model;
    }

    @RequestMapping(value = {"/{type}/page/{pageNumber}"}, method = RequestMethod.GET)
    public Model getAllPaged(HttpServletResponse response, Model model,
            @PathVariable TypeDef type, @PathVariable Integer pageNumber) throws ServletException, IOException
    {
        logger.debug("In NodeController getAll {}", type);

        if(type != null && daoService.hasDao(type.getClassName()))
        {
            GenericDao<? extends Serializable, Serializable> dao = daoService.getDao(type.getClassName());
            PagedList<? extends Serializable> list = dao.getAllPaged(PagedList.DEFAULT_ITEMS_PER_PAGE, pageNumber, null, null, false);
            
            model.addAttribute("item", list);
        }
        
        return model;
    }
    
    
    @RequestMapping(value = {"**"}, method = RequestMethod.GET)
    public ModelAndView landing(HttpServletResponse response)
    {
        logger.debug("In NodeController landing");
        return null;
        
    }

    /**
     * @param daoService the daoService to set
     */
    public void setDaoService(DaoService daoService) {
        this.daoService = daoService;
    }
    
    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    public void setBindService(BindService bindService)
    {
        this.bindService = bindService;
    }

    public void setValidatorService(ValidatorService validatorService)
    {
        this.validatorService = validatorService;
    }

    public void setHibernateDataStore(HibernateDataStore hibernateDataStore)
    {
        this.hibernateDataStore = hibernateDataStore;
    }

    public void setJackrabbitDataStore(JackrabbitDataStore jackrabbitDataStore)
    {
        this.jackrabbitDataStore = jackrabbitDataStore;
    }
    
    /**
     * Temporary method to detect store type base on id format. This only suports
     * hibernate and jackrabbit stores at the moment.
     * 
     * @param id
     * @return
     */
//    private String detectStore(String id)
//    {
//        if (IdentifierUtils.isUUID(id))
//            return TypeDef.JACKRABBIT;
//        else
//            return TypeDef.HIBERNATE;
//    }
//    
//    private DataStore getDataStore(String store)
//    {
//        if (store.equals(TypeDef.JACKRABBIT))
//            return this.jackrabbitDataStore;
//        else if (store.equals(TypeDef.HIBERNATE))
//            return this.hibernateDataStore;
//        else
//            throw new OtherObjectsException("No dataStore configured for: " + store);
//    }
 
    
    /**
     * 
     */
    private Class<? extends Serializable> getPersistentClass(String name)
    {
        Class<? extends Serializable> clazz = daoService.getDao(name).getPersistentClass();
        return clazz;
    }

    /**
     * @param beanFactory the beanFactory to set
     */
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
}
