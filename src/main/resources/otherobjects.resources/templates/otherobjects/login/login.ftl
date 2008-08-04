<#import "/oo.ftl" as oo>
<#import "/spring.ftl" as spring>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTHERobjects Workbench</title>
<@oo.css "/site/static/css/screen.css" />
</head>

<body>
 
<h1>OTHERobjects Login</h1>
 
 
 <p><@spring.message "smartFolders.label" /></p>
<@oo.showFlashMessages />

<form action="${oo.url("/j_spring_security_check")}" method="post">
<fieldset>
<p>Username:<br/>
<input type="text" name="j_username"/></p>

<p>Password:<br/>
<input type="password" name="j_password"/></p>

<p>Language:<br/>
<select name="lang"></select></p>

<p>Keep me logged in (until I manually log out)?<br/>
<input type="checkbox" name="_spring_security_remember_me"/></p>


<p><input type="Submit" value="Login" /></p>

</fieldset>
</form>


<h2>Forgotten your password?</h2>
<form action="${oo.url("/otherobjects/login/request-password-change")}" method="post">
<fieldset>
<p>Username:<br/>
<input type="text" name="username" /></p>

<p><input type="submit" value="Request password change" /></p>
</fieldset>
</form>


</body>

