<#import "/oo.ftl" as oo />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTHERobjects: $!config.getProperty("site.label")</title>
<@oo.css "/otherobjects/static/legacy/workbench.css" />
</head>
<body>
<div class="oo-header">
<h1>Workbench</h1>
<ul>
<li><strong><a href="">${oo.msg("site.editor")}</a></strong></li>
</ul>
</div>
<div class="oo-main-outer"><div class="oo-main-inner">
<@oo.showFlashMessages />