<#import "/oo.ftl" as oo />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>${pageTitle!} | ${oo.msg("site.name")} | OTHERobjects</title>
<@oo.css "/otherobjects/static/legacy/workbench.css" />

<@oo.js "/otherobjects/static/legacy/yui-2.6.0-all-min.js" />
<@oo.js "/otherobjects/static/legacy/ojay-r307-all-min.js" />


</head>
<body>
<div class="oo-header">
<h1>Workbench</h1>
<ul>
<li><strong><a href="">${oo.msg("site.editor")}</a></strong></li>
</ul>
<form action="${oo.url("/otherobjects/workbench/search")}"><input id="OoGlobalSearch" type="search" name="q" results="4" placeholder="Global Search" value="${RequestParameters.q!?html}"/></form>
</div>
<div class="oo-main-outer"><div class="oo-main-inner">
<@oo.showFlashMessages />