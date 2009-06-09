<#assign pageTitle = "Welcome" />

<#include "/otherobjects/templates/workbench/shared/header.ftl" />

<div id="ooContent">
<div class="oo-content">

<#--
<p>Drag this link: 
<a href="javascript:location.href='http://del.icio.us/post?v=4;url='+encodeURIComponent(location.href)+';title='+encodeURIComponent(document.title.replace(/^\s*|\s*$/g,''))" 
title="post to del.icio.us" onclick="window.alert('Drag this link to your bookmarks bar, or right-click it and choose Add Link to Bookmarks...');return false;"
class="bookmarklet">Add to OTHERobjects</a> up to your Bookmarks Toolbar.</p>



#--
<div class="welcome-block">
<h1>Bookmarklets</h1>
<p>Drag this link: 
<a href="javascript:location.href='http://localhost:8080/go/workbench/bookmarklet?v=1&url='+encodeURIComponent(location.href)+'&title='+encodeURIComponent(document.title.replace(/^\s*|\s*$/g,''))" 
title="post to del.icio.us" onclick="window.alert('Drag this link to your bookmarks bar, or right-click it and choose Add Link to Bookmarks...');return false;"
class="bookmarklet">Add to OTHERobjects</a> up to your Bookmarks Toolbar.</p>
</div>
--

-->


<h2>Current items in progress</h2>

<#assign edits = daoTool.get("baseNode").pageByJcrExpression("/jcr:root/site//(*, oo:node) [@published = 'false'] order by @modificationTimestamp descending",10,1) >

<ul>
<#list edits.items as edit>
    <li class="published-false><a href="${edit.linkPath}">${edit.label!} </a>
    <small>at ${edit.modificationTimestamp?datetime?string("HH:mm 'on' d MMM yyyy")}</small></li>
</#list>
</ul>

<h2>Recently edited pages</h2>
<p>Here are the last few changes to pages across the site:</p>
<#assign latestChanges = daoTool.get("baseNode").pageByJcrExpression("/jcr:root/site//element(*, oo:node) order by @modificationTimestamp descending",10,1) >
<ul>
<#list latestChanges.items as change>
    <li class="published-true><a href="${change.linkPath}">${change.label!} </a> 
    <small>at ${change.modificationTimestamp?datetime?string("HH:mm 'on' d MMM yyyy")} by ${change.modifier!}</small></li>
</#list>
</ul>

</div>
</div>

<#include "/otherobjects/templates/workbench/shared/footer.ftl" />

