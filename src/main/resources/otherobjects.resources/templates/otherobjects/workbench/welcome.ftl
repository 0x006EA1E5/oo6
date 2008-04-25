<div class="welcome">
<div class="welcome-col">

<div class="welcome-block">
<h1>Welcome to OTHERobjects CMS Milestone 4</h1>
<p>This is the fourth and final milestone release of the next generation of OTHERobjects.</p>
<p>Note that this workbench interface in only a prototype. The final version will 
look different but will contain similar functionality.</p>
<#--
<p>Drag this link: 
<a href="javascript:location.href='http://del.icio.us/post?v=4;url='+encodeURIComponent(location.href)+';title='+encodeURIComponent(document.title.replace(/^\s*|\s*$/g,''))" 
title="post to del.icio.us" onclick="window.alert('Drag this link to your bookmarks bar, or right-click it and choose Add Link to Bookmarks...');return false;"
class="bookmarklet">Add to OTHERobjects</a> up to your Bookmarks Toolbar.</p>
-->
</div>

<div class="welcome-block">
<h1>My items in progress</h1>

<#--
#set($edits = $daoService.getDao("dynaNode").pageByJcrExpression("/jcr:root/site//(*, oo:node) [@published = 'false' and not(jcr:like(@ooType,'%MetaData'))] order by @modificationTimestamp descending",10,1))
#if($edits.itemTotal>0)
<p>These are the items that have been edited by you but not published.</p>
<ul class="states">
#foreach($edit in $edits)
<li class="published-$edit.published"><a href="$edit.linkPath">$edit.label</a>
<small>at $dateTool.format("HH:mm", $edit.modificationTimestamp) by $edit.userName</small></li>
#end
</ul>
#else
<p>You have no items in progress.</p>
#end
-->

</div>

<#--
<div class="welcome-block">
<h1>Bookmarklets</h1>
<p>Drag this link: 
<a href="javascript:location.href='http://localhost:8080/go/workbench/bookmarklet?v=1&url='+encodeURIComponent(location.href)+'&title='+encodeURIComponent(document.title.replace(/^\s*|\s*$/g,''))" 
title="post to del.icio.us" onclick="window.alert('Drag this link to your bookmarks bar, or right-click it and choose Add Link to Bookmarks...');return false;"
class="bookmarklet">Add to OTHERobjects</a> up to your Bookmarks Toolbar.</p>
</div>
-->

</div>
<div class="welcome-col">
<div class="welcome-block">
<h1>What happened recently</h1>
<p>Here are the last few changes to pages across the site:</p>

<#--
#set($changes = $daoService.getDao("dynaNode").pageByJcrExpression("/jcr:root/site//element(*, oo:node) order by @modificationTimestamp descending",10,1))

<ul class="states">
#foreach($change in $changes)
<li class="published-$change.published"><a href="$edit.linkPath">$change.label</a>
<small> at $dateTool.format("HH:mm", $change.modificationTimestamp) by $change.userName
#if($change.comment)<br/>$change.comment#end
</small></li>
#end
</ul>
-->

</div>

</div>
</div>
