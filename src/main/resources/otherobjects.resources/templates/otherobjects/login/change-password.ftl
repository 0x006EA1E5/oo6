<#import "/oo.ftl" as oo>
<#import "/spring.ftl" as spring>

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
</head>

<body>
<h2>Change your password</h2>

<form method="post">

<input type="text" name="changeRequestCode" value="${crc}" />

<fieldset>

<p>
  <label for="test">New password:</label><br/>
  <@spring.bind "form.newPassword" /> 
  <@spring.formInput "form.newPassword" />
  <#list spring.status.errorMessages as error> <b>${error}</b> <br> </#list> 
</p>

<p>
  <label for="test">New password confirmation:</label><br/>
  <@spring.bind "form.newPasswordRepeated" /> 
  <@spring.formInput "form.newPasswordRepeated" />
  <#list spring.status.errorMessages as error> <b>${error}</b> <br> </#list> 
</p>

<p><input type="submit" value="Change password" /></p>
</fieldset>
</form>
</body>
</html>