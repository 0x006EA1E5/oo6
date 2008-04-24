<#import "/oo.ftl" as oo>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTHERobjects Workbench</title>
<@oo.js "/otherobjects/static/libs/firebug/firebug.js"/>
<@oo.js "/otherobjects/static/libs/extjs/src/ext-base.js"/>
<@oo.js "/otherobjects/static/libs/extjs/src/ext-all-debug.js"/>
<@oo.js "/otherobjects/static/javascript/workbench.js"/>
<@oo.js "/otherobjects/static/javascript/navigator.js"/>
<@oo.js "/otherobjects/static/javascript/grid.js"/>
<@oo.js "/otherobjects/static/javascript/edit.js"/>
<@oo.js "/otherobjects/static/javascript/preview.js"/>
<@oo.js "/otherobjects/static/javascript/list-field.js"/>
<@oo.js "/otherobjects/static/javascript/chooser-field.js"/>
<@oo.css "/otherobjects/static/css/workbench.css"/>
<@oo.css "/otherobjects/static/libs/extjs/src/ext-all.css"/>
<script type="text/javascript">
// Sets the context path
// TODO Need to insert context path here.
OO.Workbench.setPath("");
</script>
</head>
<body>

<div id="header" class="x-layout-inactive-content">
<h1><em>OTHERobjects Workbench</em></h1>
<p style="float:right; color:#FFFFFF;">$requestContext.getMessage("interface.language.label"):

#set($workbenchLocale = $requestContext.locale)
#if($workbenchLocale == "en_GB") English #else <a href="/go/workbench/?locale=en_GB">English</a> #end |
#if($workbenchLocale == "de_DE") Deutsch #else <a href="/go/workbench/?locale=de_DE">Deutsch</a> #end 

&nbsp;&nbsp;&nbsp;

$requestContext.getMessage("login.status.label"): <strong>$user.username</strong> <a href="/go/j_acegi_logout">$requestContext.getMessage("logout.label")</a>

</p>
</div>
    
<div id="select" class="x-layout-inactive-content">
	<div id="navigator-tree"></div>
</div>

<div id="content"></div>
<div id="listing-panel"></div>

<div id="status" class="x-layout-inactive-content">
<p>OTHERobjects CMS Milestone 1</p>
</div>

</body>
</html>