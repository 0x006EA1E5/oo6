<#import "/oo.ftl" as oo>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<style>
* {padding:0px; margin:0px;}
BODY {background:#CCC;}
.message {background:#FFF; border:1px solid #000; padding:15px; font:14px Arial; width:400px; margin:100px auto 0px auto; -moz-border-radius:8px; -webkit-border-radius:8px; }
H1 {padding:0px 0px 0px 10px;}
P {padding:10px 0px 0px 10px;}
UL {padding-top:20px;}
LI {list-style:none; margin-left:20px; padding-bottom:10px;}
LI A {padding-left:20px; background:url(${oo.resourceUrl('/otherobjects/static/hud/icons/add.png')}) no-repeat 0px 0px;}
</style>
</head>
<body>
<div class="message">
<h1>Sorry! 404!</h1>
<p>No page here.</p>
<p><a href="${oo.url("/")}">Back to home page</a></p>
</div>
<@oo.authorize "ROLE_ADMIN">
<div class="message">
<h1>Since you are an editor...</h1>
<p>... maybe you would like to create one?</p>
<ul>
<#list folder.allAllowedTypes as type>
<#if type?exists>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/create/${type.name}?container=${folder.id}&code=${newCode}')}">New ${type.label} ...</a></li>
</#if>
</#list>
</ul>
</div>
</@oo.authorize>
</body>
</html>
