<#import "/oo.ftl" as oo>
<#import "/spring.ftl" as spring>

<html>
<head>
<title>Debug information</title>
<style type="text/css">
.ok {color:green;}
.fail {color:red;}
.warning {color:orange;}
.info {color:gray;}

* {margin:0px; padding:0px; border:0px;}

BODY  {padding:0px; margin:0px; background-color:#eeeeee; }

.header {float:left; width:100%; background-color:#000000; padding:5px; color:white;}
.header H1 {font:bold 130% Arial, sans-serif;  margin:0px 0px 5px 0px;}

.navbar {padding:5px; float:left; width:100%; background-color:#999999; color:white;}
.navbar P {font:75% Arial, sans-serif;}
.navbar A {padding-right:5px; color:white; text-decoration:none;}
.navbar A:hover {text-decoration:underline; color:red;}

.footer {clear:both; padding:5px;}
.footer P {font:75% Arial, sans-serif;}

/* Content */
.content {font:75% Arial, sans-serif; color:#333333; padding:10px; margin:10px; float:left; width:750px; border:1px solid #aaaaaa; background-color:white;}
.content H1 {font-size:160%; margin:0px 0px 5px 0px;}
.content H2 {font-size:130%; margin:0px 0px 5px 0px;}
.content H3 {font-size:100%; margin:0px 0px 5px 0px;}
.content P {padding:0px 0px 15px 0px; line-height:140%;}
.content UL {padding:0px 0px 10px 20px;}
.content OL {padding:0px 0px 10px 20px;}
.content LI {padding:0px 15px 5px 0px; list-style:disc;}
.content A {color:#6B91BF; text-decoration:none;}		
.content A:hover {color:#1B516F; text-decoration:underline;}
.content P.align-right {text-align:right;}
.content .small {font-size:90%; font-style:normal;}
.content TABLE {clear:both; font-size:100%; margin:0px 0px 10px 0px!important; border-collapse:collapse; width:100%;}
.content H2 {border-top:1px solid #CCCCCC; text-align:left; padding:5px; font-weight:bold; background:#999999; color:#ffffff;}
.content TR {border-bottom:1px solid #CCCCCC;}
.content TH {padding:5px 5px 5px 5px; text-align:left; background-color:#F6F6F6;}
.content TD {padding:5px 5px 0px 5px; vertical-align:top;}
.content TD P {padding:0px 0px 5px 0px;}
.content TR.even TD {background-color:#F6F6F6;}
.content TH.right, .content TD.right {text-align:right;}
.content .label {width:200px;}


.disabledLink {color:#bbbbbb}
.invalid {color:red;}
.valid {color:green;}
.listOdd {padding:2px; background-color:#eeeeee;}
.listEven {padding:2px; background-color:#ffffff;}

/* Messages and warnings*/
.warning {background:#FFE196 url($static.setPath("/otherobjects/static/graphics/warning-sign.gif")) no-repeat 10px 5px; padding:10px 10px 10px 50px!important; border:1px solid #990000; margin:10px 0px 10px 0px; font-weight:bold; color:#D02A1F;}
.panel .warning {margin:10px;}
.message {background:#BFDCFF url($static.setPath("/otherobjects/static/graphics/information-sign.gif")) no-repeat 10px 5px; padding:10px 10px 10px 50px!important; border:1px solid #3366CC; margin:10px 0px 10px 0px; font-weight:bold;}
.panel .message {margin:10px;}
.disclaimer {font-size:90%; border-top:1px solid #CCCCCC; padding:10px 0px 0px 0px; margin:50px 0px 0px 0px;}

INPUT {margin:0px 10px 5px 0px;padding:2px;}
.submit {}
.text {border:1px solid #cccccc;}
A.button {background: #cccccc; padding:3px; color:#000000;}

/* Tasks status */
.bar {float:left; height:10px; width:100px; background-color:#eeeeee;}
.bar-progress {float:left; height:10px; background-color:#ff0000;}

</style>
</head>
<body>

<div class="header">
<h1>Debug information</h1>
</div>

<div class="navbar">
<p>
<a href="${oo.url("/")}">Back to site</a> | <a href="${oo.url("/otherobjects/workbench/")}">Back to OTHERobjects</a>
</p>
</div>

<div class="content">

<h2>Status</h2>

<table>
    <tr><td class="label">User</td><td class="info"> ${(security.user.username)!} [<a href="${oo.url("/otherobjects/logout.html")}">Logout</a>]  </td></tr>
    <tr><td class="label">Admin?</td><td class="info">${security.authorize("ROLE_ADMIN",null,"ROLE_GUEST")?string("Yes","No")}</td></tr>
</table>

<h2>Session</h2>
<#if !Session?has_content>
<p>No current session</p>
<#else>
<table>
	<tr><td class="label">Session ID</td><td class="info">${session.id}</td></tr>
</table>
</#if>

<h2>Dependencies</h2>

<table>
    <#if imageMagickVersion??>
    <tr><td class="label">Imagemagick</td><td class="ok">OK ${imageMagickVersion}</td></tr>
    </#if>    
    <#if imageMagickError??>
    <tr><td class="label">Imagemagick</td><td class="fail">FAIL ${imageMagickError}</td></tr>        
    </#if>
</table>

<h2>System</h2>

<table>
    <tr><td class="label">Freemarker version</td><td class="info">${.version}</td></tr>
    <tr><td class="label">Servlet API</td><td class="info">${servletApiVersion}</td></tr>
    <tr><td class="label">Default Encoding</td><td class="info">${defaultEncoding}</td></tr>
    <tr><td class="label">System Locale</td><td class="info">${systemUserLanguage}_${systemUserCountry}</td></tr>
    <tr><td class="label">System Timezone</td><td class="info">${systemUserTimezone}</td></tr>
    <tr><td class="label">Java version</td><td class="info">${javaVersion} (${javaVendor})</td></tr>
    <tr><td class="label">System Username</td><td class="info">${systemUserName}</td></tr>
    <tr><td class="label">Operating System</td><td class="info">${systemOsName} ${systemOsVersion} (${systemOsArch})</td></tr>
    <tr><td class="label">Server Name</td><td class="info">${serverName} (${serverIp})</td></tr>
    <tr><td class="label">Memory</td><td class="info">${formatTool.formatFileSize(totalMemory)} used  ${formatTool.formatFileSize(freeMemory)} available (${formatTool.formatFileSize(maxMemory)} max)</td></tr>
    <tr><td class="label">Operating System</td><td class="info">${systemOsName} ${systemOsVersion} (${systemOsArch})</td></tr>
</table>


<h2>Connectivity</h2>

<table>
    <tr><td class="label">External connect</td><td class="info">${testExternalUrl}</td></tr>
    <tr><td class="label">Internal connect</td><td class="info">${testInternalUrl}</td></tr>
    <tr><td class="label">Datastore serving</td><td class="info">TODO</td></tr>
    <tr><td class="label">Static resource serving</td><td class="info">TODO</td></tr>
    <tr><td class="label">Email send</td><td class="info">TODO</td></tr>
</table>

<h2>Data Stores</h2>

<table>
 	<tr><td class="label">Private Data Path</td><td class="info">${privateDataPath}</td></tr>
 	<tr><td class="label">Public Data Path</td><td class="info">${publicDataPath}</td></tr>
 	<tr><td class="label">Database</td><td class="info">${dbUrl} [<a href="${oo.url("/otherobjects/debug/database")}">Explore</a>]</td></tr>
    <tr><td class="label">Database schema version</td><td class="info">${dbSchemaVersion}</td></tr>
    <tr><td class="label">JCR</td><td class="info">${jcrLocation} [<a href="${oo.url("/otherobjects/debug/jcr")}">Explore</a>]</td></tr>
    <tr><td class="label">JCR schema version</td><td class="info">${jcrSchemaVersion}</td></tr>
    <tr><td class="label">Datastore permissions</td><td class="info">TODO</td></tr>
</table>

<h2>Types</h2>

<table>
 	<tr><td class="label">All types</td><td class="info">
 	<#list types as type>
 	${type.name}<br/>
 	</#list>
 	</td></tr>
</table>


</div>
</div>


</body>
</html>