package org.otherobjects.cms.model;

import junit.framework.TestCase;

public class UserTest extends TestCase
{

    public void testGetPassword()
    {

        User u = new User();

        // New user with new password
        assertNull(u.getPassword());
        u.setPlainTextPassword("password");
        assertNotNull(u.getPassword());
        assertEquals(u.getPassword().length(), 40);
        u.setPlainTextPassword(null);

        // Normal persisted password behaviour
        u.setPassword("new-hash");
        assertEquals("new-hash", u.getPassword());

        // Change password
        u.setPlainTextPassword("new-password");
        assertFalse("new-hash".equals(u.getPassword()));
        u.setPlainTextPassword(null);

        // Modify user, don't provide new password
        String pw = u.getPassword();
        u.setPlainTextPassword("");
        assertEquals(pw, u.getPassword());

    }

}
