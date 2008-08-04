

<!--
<div class="title-oo"><div class="close-oo">X</div>OTHERobjects</div>
<div class="field-oo"><label>Title:</label><input class="text" type="text" value="Here is some test text"></div>
<div class="field-oo"><label>Metadata:</label><textarea>Here is a line of test text. Here is a line of test text. Here is a line of test text.</textarea></div>

<div class="button-oo"><img src="${resourceUrl("/otherobjects/static/icons/zoom.png")}" width"100" height="100" /><p>Search</p></div>
<div class="button-oo"><a onclick="ooEnableBlockSelector()"><img src="${resourceUrl("/otherobjects/static/icons/pencil.png")}" width"100" height="100" /><p>Edit</p></a></div>
<div class="button-oo"><img src="${resourceUrl("/otherobjects/static/icons/add.png")}" width"100" height="100" /><p>New</p></div>
<div class="button-oo"><a onclick="ooEnableBlockManagement()"><img src="${resourceUrl("/otherobjects/static/icons/palette.png")}" width"100" height="100" /><p>Design</p></a></div>
<div class="button-oo"><a href="${url("/otherobjects/workbench/")}"><img src="${resourceUrl("/otherobjects/static/icons/database-gear.png")}" width"100" height="100" /><p>Workbench</p></a></div>
<div class="button-oo"><a href="${url("/otherobjects/logout.html")}"><img src="${resourceUrl("/otherobjects/static/icons/lock.png")}" width"100" height="100" /><p>Log out</p></a></div>
-->

<div id="oo-dock" class="oo-panel">
	<div class="item"><img src="${oo.resourceUrl("/otherobjects/static/hud/icons/search.png")}" width="50" height="50" alt="Search"></div>
	<div class="item"><a onclick="ooEnableBlockSelector()"><img src="${oo.resourceUrl("/otherobjects/static/hud/icons/edit.png")}" width="50" height="50"></a></div>
	<div class="item"><img src="${oo.resourceUrl("/otherobjects/static/hud/icons/new.png")}" width="50" height="50"></div>
	<div class="item"><a href="${url("/otherobjects/logout.html")}"><img src="${oo.resourceUrl("/otherobjects/static/hud/icons/logout.png")}" width="50" height="50"></a></div>	
</div> 
