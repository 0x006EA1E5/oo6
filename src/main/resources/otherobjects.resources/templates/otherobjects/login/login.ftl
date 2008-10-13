<#import "/oo.ftl" as oo>
<#import "/spring.ftl" as spring>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTHERobjects Workbench</title>
<@oo.css "/otherobjects/static/legacy/workbench.css" />
<style>
BODY {height:1000px; background:#006DA8 url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/admin-bg.gif")});}
.oo-header {height:65px; background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/admin-top.png")});}
.oo-footer {height:65px; background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/admin-bottom.png")});}
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

<h1><img src="${oo.resourceUrl("/otherobjects/static/legacy/graphics/logo-login.gif")}" height="60" width="250" alt="OTHER Objects" />
</h1>



<h2 class="oo-indent">Please Login</h2>

<@oo.showFlashMessages />

<form action="${oo.url("/j_spring_security_check")}" method="post">
<fieldset>
<p><label>Username:</label>
<input type="text" name="j_username"/></p>

<p><label>Password:</label>
<input type="password" name="j_password"/></p>

<p><label>Language:</label>
<select name="lang"></select></p>

<p><label>&nbsp;</label>
<input type="checkbox" name="_spring_security_remember_me"/>Keep me logged in (until I manually log out)?<br/></p>

<p><label>&nbsp;</label><input type="Submit" value="Login" /></p>

</fieldset>
</form>


<h2 class="oo-indent">Forgotten your password?</h2>
<form action="${oo.url("/otherobjects/login/request-password-change")}" method="post">
<fieldset>
<p><label>Username:</label>
<input type="text" name="username" /></p>

<p><label>&nbsp;</label><input type="submit" value="Request password change" /></p>
</fieldset>
</form>


</div>
</div>

<div class="oo-build">
<p>Version TODO</p> 
</div>

<div class="oo-footer">
</div>

</body>
</html>