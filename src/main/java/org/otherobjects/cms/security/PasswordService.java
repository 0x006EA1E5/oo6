package org.otherobjects.cms.security;

import org.otherobjects.cms.model.PasswordChangeRequest;
import org.otherobjects.cms.model.User;

public interface PasswordService
{
    String generatePasswordChangeRequestCode(String username) throws Exception;

    String generatePasswordChangeRequestCode(User user) throws Exception;

    PasswordChangeRequest getPasswordChangeRequest(String changeRequestCode);

    boolean validateChangeRequestCode(String changeRequestCode);

    boolean changePassword(User user, String oldPassword, String newPassword);

    boolean changePassword(PasswordChanger passwordChanger);

    void cleanExpiredPasswordChangeRequests();

    boolean isPasswordMatch(User user, String password);
}
