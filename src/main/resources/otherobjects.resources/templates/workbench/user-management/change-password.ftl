<#import "/oo.ftl" as oo>
<#import "/spring.ftl" as spring>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Change password | <@spring.message "site.name" /> | OTHERobjects</title>
<@oo.css "/otherobjects/static/workbench/workbench.css" />
<style>
BODY {height:2000px; background:#006DA8 url(${oo.resourceUrl("/otherobjects/static/workbench/assets/admin-bg.gif")});}
.oo-header {height:65px; background:url(${oo.resourceUrl("/otherobjects/static/workbench/assets/admin-top.png")});}
.oo-footer {height:65px; background:url(${oo.resourceUrl("/otherobjects/static/workbench/assets/admin-bottom.png")});}
.oo-content {padding:20px 155px 0px 180px; font:70% Arial; background:#FFFFFF;}
.oo-build {padding:20px 20px 20px 180px; font:70% Arial; text-align:right; color:#999999; background:#FFFFFF;}
.oo-indent {margin-left:55px;}
.oo-flash-error {padding:7px 0px 7px 30px!important;}
</style>
</head>
<body>
<div class="oo-header">
</div>

<div class="col100pc">
<div class="oo-content">

<h1><img src="${oo.resourceUrl("/otherobjects/static/workbench/assets/logo-login.gif")}" height="60" width="250" alt="OTHER Objects" /></h1>

<h2 class="oo-indent">Enter your new password</h2>
<p class="oo-indent">Passwords must be at least 6 characters long and contain at least one number.</p>

<div class="admin"><@oo.showFlashMessages /></div>

<form method="post">
<fieldset>

<#--
If we did not pick up a valid CRC from the paramters show a field 
so that the user can enter it manually. 
-->
<@spring.bind "passwordChanger.changeRequestCode" /> 
<#if validCrc?? && validCrc>
<@spring.formHiddenInput "passwordChanger.changeRequestCode" />
<#else>
<p>
  <label for="test">CRC:</label>
  <@spring.formInput "passwordChanger.changeRequestCode" />
</p>
</#if>

<p>
  <label for="test">New:</label>
  <@spring.bind "passwordChanger.newPassword" /> 
  <@spring.formPasswordInput "passwordChanger.newPassword" />
  <#list spring.status.errorMessages as error> <span class="oo-field-error">${error}</span> <br> </#list> 
</p>

<p>
  <label for="test">Confirm:</label>
  <@spring.bind "passwordChanger.newPasswordRepeated" /> 
  <@spring.formPasswordInput "passwordChanger.newPasswordRepeated" />
  <#list spring.status.errorMessages as error> <span class="oo-field-error">${error}</span> <br> </#list> 
</p>

<p><label>&nbsp;</label><input type="submit" value="Change password" /></p>

</fieldset>
</form>

<p></p>

</div>
<div class="oo-footer">
</div>

</body>
</html>