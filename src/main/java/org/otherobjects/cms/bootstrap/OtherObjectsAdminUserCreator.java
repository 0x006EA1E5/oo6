package org.otherobjects.cms.bootstrap;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.model.Role;
import org.otherobjects.cms.model.RoleDao;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.model.UserDao;
import org.otherobjects.cms.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;

public class OtherObjectsAdminUserCreator
{
    private final Logger logger = LoggerFactory.getLogger(OtherObjectsAdminUserCreator.class);
    public static final String DEFAULT_ADMIN_USER_NAME = "admin";
    public static final String DEFAULT_ADMIN_ROLE_NAME = "ROLE_ADMIN";
    public static final String DEFAULT_USER_ROLE_NAME = "ROLE_USER";
    private static final int GENERATED_PASSWORD_LENGTH = 6;

    private UserDao userDao;
    private RoleDao roleDao;
    private PasswordEncoder passwordEncoder;
    private SaltSource saltSource;
    private String generatedAdminPassword;

    public void setPasswordEncoder(PasswordEncoder passwordEncoder)
    {
        this.passwordEncoder = passwordEncoder;
    }

    public User createAdminUser()
    {
        logger.debug("Creating Admin user and roles.");
        Role role = new Role(DEFAULT_ADMIN_ROLE_NAME, "Adminstrator role");
        role = roleDao.save(role);
        Role role2 = new Role(DEFAULT_USER_ROLE_NAME, "User role");
        role2 = roleDao.save(role2);

        List<Role> roles = new ArrayList<Role>();
        roles.add(role);
        roles.add(role2);

        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setFirstName("The");
        adminUser.setLastName("Administrator");
        adminUser.setEmail("");
        adminUser.setRoles(roles);
        adminUser.setAccountExpired(false);
        adminUser.setAccountLocked(false);
        adminUser.setEnabled(true);
        adminUser.setPasswordHint("See the command line output for the temporary admin password.");
        
        resetPassword(adminUser);
        adminUser = userDao.save(adminUser);
        return adminUser;
    }
    
    public void resetPassword(User user)
    {
        // Generate password 
        generatedAdminPassword = SecurityUtils.generatePassword(GENERATED_PASSWORD_LENGTH);
        user.setPassword(passwordEncoder.encodePassword(generatedAdminPassword, saltSource.getSalt(user)));
        user = userDao.save(user);
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setRoleDao(RoleDao roleDao)
    {
        this.roleDao = roleDao;
    }

    public void setSaltSource(SaltSource saltSource)
    {
        this.saltSource = saltSource;
    }

    public String getGeneratedAdminPassword()
    {
        return generatedAdminPassword;
    }

   
}
