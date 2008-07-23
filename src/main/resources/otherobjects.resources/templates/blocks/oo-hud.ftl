

<!--
<div class="title-oo"><div class="close-oo">X</div>OTHERobjects</div>
<div class="field-oo"><label>Title:</label><input class="text" type="text" value="Here is some test text"></div>
<div class="field-oo"><label>Metadata:</label><textarea>Here is a line of test text. Here is a line of test text. Here is a line of test text.</textarea></div>
-->

<div class="button-oo"><img src="${resourceUrl("/otherobjects/static/icons/zoom.png")}" width"100" height="100" /><p>Search</p></div>
<div class="button-oo"><a onclick="ooEnableBlockSelector()"><img src="${resourceUrl("/otherobjects/static/icons/pencil.png")}" width"100" height="100" /><p>Edit</p></a></div>
<div class="button-oo"><img src="${resourceUrl("/otherobjects/static/icons/add.png")}" width"100" height="100" /><p>New</p></div>
<div class="button-oo"><a onclick="ooEnableBlockManagement()"><img src="${resourceUrl("/otherobjects/static/icons/palette.png")}" width"100" height="100" /><p>Design</p></a></div>
<div class="button-oo"><a href="${url("/otherobjects/workbench/")}"><img src="${resourceUrl("/otherobjects/static/icons/database-gear.png")}" width"100" height="100" /><p>Workbench</p></a></div>
<div class="button-oo"><a href="${url("/otherobjects/logout.html")}"><img src="${resourceUrl("/otherobjects/static/icons/lock.png")}" width"100" height="100" /><p>Log out</p></a></div>

</div>


<#--

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


-->