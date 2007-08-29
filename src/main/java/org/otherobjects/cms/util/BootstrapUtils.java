package org.otherobjects.cms.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.io.IOUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.UserDao;
import org.otherobjects.cms.model.Role;
import org.otherobjects.cms.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Bootstrap utility that runs on site start up. Ensures all initial config data 
 * is correctly initalised.
 * 
 * <p>This curretntly does this by runnig the script in <code>bootstrap-data/setup.groovy</code>.
 * 
 * @author rich
 */
public class BootstrapUtils
{
    private final Logger logger = LoggerFactory.getLogger(BootstrapUtils.class);

    private DaoService daoService;
    private Resource bootstrapScript;

    public void setBootstrapScript(Resource bootstrapScript)
    {
        this.bootstrapScript = bootstrapScript;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    /**
     * Run the bootstrap script.
     */
    public void bootstrap()
    {
        // FIXME Run on first startup only

        try
        {
            createUser();

            UserDao userDao = (UserDao) daoService.getDao(User.class);
            UserDetails adminUser = userDao.loadUserByUsername("admin");

            // FIXME Can this be done in a cleaner way?
            Authentication authentication = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            runEssentialScripts();
        }
        catch (Exception e)
        {
            logger.error("Could not bootstrap data.", e);
            throw new OtherObjectsException("Could not bootstrap data.", e);
        }
        finally
        {
            // Log out
            SecurityContextHolder.clearContext();
        }
    }

    private void createUser()
    {
        Role role = new Role("ROLE_ADMIN", "Adminstrator role");
        role = (Role) daoService.getDao(Role.class).save(role);
        Role role2 = new Role("ROLE_USER", "User role");
        role2 = (Role) daoService.getDao(Role.class).save(role2);

        Set<Role> roles = new HashSet();
        roles.add(role);
        roles.add(role2);

        User adminUser = new User();
        //adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setFirstName("Administrator");
        adminUser.setLastName("User");
        adminUser.setPassword("d033e22ae348aeb5660fc2140aec35850c4da997");
        adminUser.setEmail("admin");
        adminUser.setRoles(roles);
        adminUser.setAccountExpired(false);
        adminUser.setAccountLocked(false);
        adminUser.setEnabled(true);
        adminUser.setPasswordHint("See the command line output for the temporary admin password.");
        daoService.getDao(User.class).save(adminUser);
    }

    private void runEssentialScripts() throws IOException
    {
        //        BSFManager manager = new BSFManager();
        //        manager.declareBean("daoService", daoService, daoService.getClass());
        //        manager.eval("groovy", "script.groovy", 0, 0, script);

        Binding binding = new Binding();
        binding.setVariable("daoService", daoService);
        GroovyShell shell = new GroovyShell(binding);
        String script = IOUtils.toString(bootstrapScript.getInputStream());
        shell.evaluate(script);
    }
}
