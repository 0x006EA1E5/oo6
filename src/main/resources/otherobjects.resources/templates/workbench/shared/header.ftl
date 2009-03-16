<#import "/oo.ftl" as oo />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" class="oo">
<head>
<title>${pageTitle!} | ${oo.msg("site.name")} | OTHERobjects</title>

<@oo.css "/otherobjects/static/hud/toolbar.css" />
<@oo.css "/otherobjects/static/workbench/workbench.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.6.0/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.6.0/build/treeview/assets/skins/sam/treeview.css" />

<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/selector/selector-beta.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/connection/connection.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/animation/animation.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/treeview/treeview-min.js"></script>
<script type="text/javascript" src="http://cdn.othermedia.com/ojay/0.3.0/all-min.js"></script>
<@oo.js "/otherobjects/static/workbench/workbench.js" />

</head>
<body>

<@oo.showFlashMessages />

<#include "/otherobjects/templates/hud/toolbar.ftl" />