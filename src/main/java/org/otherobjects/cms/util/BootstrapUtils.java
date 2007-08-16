package org.otherobjects.cms.util;

import java.io.File;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.bsf.BSFManager;
import org.apache.commons.io.FileUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.UserDao;
import org.otherobjects.cms.model.User;

/**
 * Bootstrp utility that runs on site start up. Ensures all initial config data 
 * is correctly initalised.
 * 
 * <p>This curretntly does this by runnig the script in <code>bootstrap-data/setup.groovy</code>.
 * 
 * @author rich
 */
public class BootstrapUtils
{
    private static final String BOOTSTRAP_DATA_SCRIPT = "bootstrap-data/setup.groovy";
    private DaoService daoService;

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }
    
    /**
     * Run the bootstrap script.
     */
    public void bootstrap()
    {
        try
        {
            UserDao userDao = (UserDao) daoService.getDao(User.class);
            User adminUser = userDao.get(1L);   
            
            // FIXME Can this be done in a cleaner way?
            Authentication authentication = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // FIXME This should load via the classpath
            String script = FileUtils.readFileToString(new File(BOOTSTRAP_DATA_SCRIPT), "UTF-8");
            BSFManager manager = new BSFManager();
            manager.declareBean("daoService", daoService, daoService.getClass());
            manager.eval("groovy", "script.groovy", 0, 0, script);
        }
        catch (Exception e)
        {
         
            throw new OtherObjectsException("Could not bootstrap data.",e);
        }
        finally
        {
            // Log out
            SecurityContextHolder.clearContext();

        }
    }
}
