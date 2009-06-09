<#import "/oo.ftl" as oo />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" class="oo">
<head>
<title>${pageTitle!} | ${oo.msg("site.name")} | OTHERobjects</title>

<@oo.css "/otherobjects/static/workbench/toolbar.css" />
<@oo.css "/otherobjects/static/workbench/workbench.css" />

<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/utilities/utilities.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/cookie/cookie-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/datasource/datasource-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/json/json-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/selector/selector-min.js"></script> 
<script type="text/javascript" src="http://cdn.othermedia.com/ojay/0.3.1/all-min.js"></script>
<@oo.js "/otherobjects/static/workbench/workbench.js" />

</head>

<body>
<div class="oo-header">
<h1>Workbench</h1>
<ul>
<li><#if url?contains("overview.html")><strong></#if><a href="${oo.url("/otherobjects/workbench/overview.html")}">${oo.msg("dashboard.tab")}</a><div class="oo-small-badge"><div class="oo-small-badge-label">2</div></div><#if url?contains("overview.html")></strong></#if></li>
<li><#if ! url?contains("overview.html")><strong></#if><a href="${oo.url("/otherobjects/workbench/list/")}">${oo.msg("site.manager")}</a><#if ! url?contains("overview.html")></strong></#if></li>
<#--<li><a href="">${oo.msg("site.designer")}</a></li>-->
<#--<li><a href="">${oo.msg("user.management")}</a></li>-->
</ul>
<form action="${oo.url("/otherobjects/workbench/search")}"><input id="OoGlobalSearch" type="search" name="q" results="4" placeholder="Global Search" value="${RequestParameters.q!?html}"/></form>
</div>
<div class="oo-main-outer"><div class="oo-main-inner">
<@oo.showFlashMessages />
