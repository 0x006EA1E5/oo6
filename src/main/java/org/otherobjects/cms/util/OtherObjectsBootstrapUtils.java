package org.otherobjects.cms.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.apache.commons.io.IOUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.Role;
import org.otherobjects.cms.model.RoleDao;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.model.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Creates standard repository structure and ensures there is at least one admin user.
 * 
 * <p>This currently does this by running the script in <code>bootstrap-data/setup.groovy</code>.
 * 
 * @author rich
 */
public class OtherObjectsBootstrapUtils
{
    private final Logger logger = LoggerFactory.getLogger(OtherObjectsBootstrapUtils.class);

    private UserDao userDao;
    private RoleDao roleDao;
    private UniversalJcrDao universalJcrDao;
    private Resource bootstrapScript;

    /**
     * Run the bootstrap script.
     */
    @PostConstruct
    public void bootstrap()
    {
        // FIXME Run on first startup only
        try
        {
            UserDetails adminUser = userDao.loadUserByUsername("admin");

            // Create admin user if one does not exist and then run setup script
            if (adminUser == null)
            {
                adminUser = createUser();
                
                // Authenticate as new Admin user
                Authentication authentication = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                runScript(this.bootstrapScript.getInputStream());
                //if (!standalone)
                //    runScript(new FileInputStream("src/main/resources/site.resources/bootstrap-data/setup.script"));
            }
        }
        catch (Exception e)
        {
            this.logger.error("Could not bootstrap data.", e);
            throw new OtherObjectsException("Could not bootstrap data.", e);
        }
        finally
        {
            // Log out
            SecurityContextHolder.clearContext();
        }
    }

    protected User createUser()
    {
        logger.debug("Creating Admin user and roles.");
        Role role = new Role("ROLE_ADMIN", "Adminstrator role");
        role = (Role) roleDao.save(role);
        Role role2 = new Role("ROLE_EDITOR", "Editor role");
        role2 = (Role) roleDao.save(role2);

        List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(role);
        roles.add(role2);

        User adminUser = new User();
        //adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setFirstName("The");
        adminUser.setLastName("Administrator");
        adminUser.setPassword("d033e22ae348aeb5660fc2140aec35850c4da997");
        adminUser.setEmail("admin@mycompany.com");
        adminUser.setRoles(roles);
        adminUser.setAccountExpired(false);
        adminUser.setAccountLocked(false);
        adminUser.setEnabled(true);
        adminUser.setPasswordHint("See the command line output for the temporary admin password.");
        return (User) userDao.save(adminUser);
    }

    protected void runScript(InputStream is) throws IOException
    {
        logger.debug("Running setup scripts.");
        Binding binding = new Binding();
        binding.setProperty("universalJcrDao", universalJcrDao);
        GroovyShell shell = new GroovyShell(binding);
        String script = IOUtils.toString(is);
        shell.evaluate(script);
    }

    public void setBootstrapScript(Resource bootstrapScript)
    {
        this.bootstrapScript = bootstrapScript;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setRoleDao(RoleDao roleDao)
    {
        this.roleDao = roleDao;
    }

    public void setUniversalJcrDao(UniversalJcrDao universalJcrDao)
    {
        this.universalJcrDao = universalJcrDao;
    }
}
