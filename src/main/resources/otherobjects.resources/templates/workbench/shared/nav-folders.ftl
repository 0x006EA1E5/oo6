<#macro ooFolderNavList treeItem>
<#if treeItem.children?has_content>
<ul>
<#list treeItem.children as f><li class="expanded"><a href="${oo.url("/otherobjects/workbench/list/"+f.id)}">${oo.msg(f.label)}</a><@ooFolderNavList f/></li></#list>
</ul>
</#if>
</#macro>

<div id="ooNavigation" class="oo-panel oo-text-style">
<ul class="oo-folders">
<li class="expanded"><a href="${oo.url("/otherobjects/")}">OTHERobjects</a>
<@ooFolderNavList daoTool.get("folder").folderTree />
</li>
</ul>
</div>

<script>
navTree = new YAHOO.widget.TreeView("ooNavigation");
navTree.render();
navTree.subscribe("clickEvent", function(node) { 
	window.location = node.node.href; 
	return false;
}); 
</script>
