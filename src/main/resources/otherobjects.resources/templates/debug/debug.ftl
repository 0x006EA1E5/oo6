<#import "/oo.ftl" as oo>
<#import "/spring.ftl" as spring>

<html>
<head>
<title>Debug information</title>
</head>
<body>

<style type="text/css">
.ok {color:green;}
.fail {color:red;}
.warning {color:orange;}
.info {color:gray;}
</style>

<h1>System Information</h1>

<h2>Status</h2>

<table>
    <tr><td>User</td><td class="info"> ${(security.user.username)!} [<a href="/logout.html">Logout</a>]  </td></tr>
    <tr><td>Admin?</td><td class="info">${security.authorize("ROLE_ADMIN",null,"ROLE_GUEST")?string("Yes","No")}</td></tr>
</table>

<h2>Session</h2>
<#if !Session?has_content>
<p>No current session</p>
<#else>
<table>
	<tr><td>Session ID</td><td class="info">${session.id}</td></tr>
</table>
</#if>

<h2>System</h2>

<table>
    <#if imageMagickVersion??>
    <tr><td>Imagemagick</td><td class="ok">OK ${imageMagickVersion}</td></tr>
    </#if>    
    <#if imageMagickError??>
    <tr><td>Imagemagick</td><td class="fail">FAIL ${imageMagickError}</td></tr>        
    </#if>
    <tr><td>Freemarker version</td><td class="info">${.version}</td></tr>
    <tr><td>Servlet API</td><td class="info">${servletApiVersion}</td></tr>
    <tr><td>Default Encoding</td><td class="info">${defaultEncoding}</td></tr>
    <tr><td>System Locale</td><td class="info">${systemUserLanguage}_${systemUserCountry}</td></tr>
    <tr><td>System Timezone</td><td class="info">${systemUserTimezone}</td></tr>
    <tr><td>Java version</td><td class="info">${javaVersion} (${javaVendor})</td></tr>
    <tr><td>System Username</td><td class="info">${systemUserName}</td></tr>
    <tr><td>Operating System</td><td class="info">${systemOsName} ${systemOsVersion} (${systemOsArch})</td></tr>
</table>


<h2>Connectivity</h2>

<table>
    <tr><td>External connect</td><td class="info">${testExternalUrl}</td></tr>
    <tr><td>Internal connect</td><td class="info">${testInternalUrl}</td></tr>
    <tr><td>Datastore serving</td><td class="info">TODO</td></tr>
    <tr><td>Static resource serving</td><td class="info">TODO</td></tr>
    <tr><td>Email send</td><td class="info">TODO</td></tr>
</table>

<h2>Data Stores</h2>

<table>
 	<tr><td>Private Data Path</td><td class="info">${privateDataPath}</td></tr>
 	<tr><td>Public Data Path</td><td class="info">${publicDataPath}</td></tr>
 	<tr><td>Database</td><td class="info">${dbUrl} [<a href="${oo.url("/otherobjects/debug/database")}">Explore</a>]</td></tr>
    <tr><td>Database schema version</td><td class="info">${dbSchemaVersion}</td></tr>
    <tr><td>JCR</td><td class="info">${jcrLocation} [<a href="${oo.url("/otherobjects/debug/jcr")}">Explore</a>]</td></tr>
    <tr><td>JCR schema version</td><td class="info">${jcrSchemaVersion}</td></tr>
    <tr><td>Datastore permissions</td><td class="info">TODO</td></tr>
</table>

</body>
</html>