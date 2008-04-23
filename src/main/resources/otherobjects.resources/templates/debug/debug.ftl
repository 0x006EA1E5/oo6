<style type="text/css">
.ok {color:green;}
.fail {color:red;}
.warning {color:orange;}
.info {color:gray;}
</style>


<h1>System Information</h1>

<h2>Status</h2>

<table>
    <tr><td>User</td><td class="info">Guest User? [<a href="">Logout</a>]</td></tr>
</table>

<h2>Pre-requisites</h2>

<p>This section tests that all OO dependencies are available.</p>

<table>
    <tr><td>Database</td><td class="info">Unchecked [<a href="/otherobjects/debug/database">Explore</a>]</td></tr>
    <tr><td>JCR</td><td class="info">Unchecked [<a href="/otherobjects/debug/jcr">Explore</a>]</td></tr>
    <#if imageMagickVersion??>
    <tr><td>Imagemagick</td><td class="ok">OK ${imageMagickVersion}</td></tr>
    </#if>    
    <#if imageMagickError??>
    <tr><td>Imagemagick</td><td class="fail">FAIL ${imageMagickError}</td></tr>        
    </#if>
    <tr><td>Datastore serving</td><td class="info">Unchecked</td></tr>
    <tr><td>Datastore permissions</td><td class="info">Unchecked</td></tr>
    <tr><td>Static resource serving</td><td class="info">Unchecked</td></tr>
</table>

<h2>Information</h2>

<table>
    <tr><td>Database schema version</td><td class="info">Unknown</td></tr>
    <tr><td>JCR schema version</td><td class="info">Unknown</td></tr>
    <tr><td>Java version</td><td class="info">Unknown</td></tr>
</table>

<h2>Types</h2>

<table border="1">
    <thead>
        <tr>
            <th>Type</th>
            <th>Class</th>
        </tr>
    </thead>
    <tr>
     <td>{type.name}</td>
     <td>{type.className}</td>
    </tr>
</table>