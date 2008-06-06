<h1>OTHERobjects</h1>
<form><input type="search" class="search" name="q"/></form>

<div class="oo-hud-button" id="oo-page-info">
<h1>${resourceObject.label}</h1>
<p>${resourceObject.linkPath}</p>
<img style="float:left; padding-right:10px;" src="http://www.gravatar.com/avatar/f3393615ce87349a39acb05efb134314.jpg?s=40" width="40" height="40"/>
<p style="float:left;">Last edited by  <strong>${resourceObject.userName}</strong><br/>
at <strong>${resourceObject.modificationTimestamp?datetime?string("HH:mm 'on' d MMMM yyyy")} </strong></p>
<p style="float:left; clear:both;"><br/>Template: <strong>${ooTemplate.label}</strong></p>
<p style="float:left; clear:both;">Main object: <strong>${resourceObject.typeDef.label}</strong></p>
</div>


<a href="/otherobjects/workbench/" class="oo-hud-button" id="oo-debug">Workbench</a>
<a onclick="ooEnableBlockSelector()" class="oo-hud-button" id="oo-edit">Edit block contents</a>
<a href="/otherobjects/debug/debug" class="oo-hud-button" id="oo-workbench">Debug page</a>
<a onclick="ooEnableBlockManagement()" class="oo-hud-button" id="oo-manage-blocks">Manage blocks</a>
<a class="oo-hud-button" id="oo-logout">Log out</a>
<a class="oo-hud-button" id="oo-publish">Publish</a>
