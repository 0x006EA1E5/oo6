package org.otherobjects.cms.security;

public class PasswordChanger
{
    private String changeRequestCode;
    private String newPassword;
    private String newPasswordRepeated;

    public boolean newPasswordValid()
    {
        if (newPassword != null)
            return newPassword.equals(newPasswordRepeated);
        else
            return false;
    }

    public String getChangeRequestCode()
    {
        return changeRequestCode;
    }

    public void setChangeRequestCode(String changeRequestCode)
    {
        this.changeRequestCode = changeRequestCode;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    public String getNewPasswordRepeated()
    {
        return newPasswordRepeated;
    }

    public void setNewPasswordRepeated(String newPasswordRepeated)
    {
        this.newPasswordRepeated = newPasswordRepeated;
    }

}
