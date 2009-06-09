
<#macro ooFolderNavList treeItem>
<#if treeItem.children?has_content>
<ul>
<#list treeItem.children as f>
	<#--
	#if($parameters.folder.isChildOf($f) || $parameters.folder == $f)
		#if($f.allChildren.size() >0)
			class="oo-folder-full-open"
		#else
			class="oo-folder-empty-open"
		#end
	#end>
	#else
	-->
	
	<li class="<#if f.children?has_content>oo-folder-empty<#else>oo-folder-empty</#if> <#if f.object.published!>oo-folder-published<#else>oo-folder-in-progress</#if>">
	<span></span><a href="${oo.url("/otherobjects/workbench/list/"+f.id)}">${oo.msg(f.label)}</a>	
	<@ooFolderNavList f/>
	</li>

</#list>
</ul>
</#if>
</#macro>

<div class="oo-navigation">
<ul class="oo-folders">
<li class="oo-folder-full-open"><span onclick="ooExpandContract(this)"></span><a href="">OTHERobjects</a>
<@ooFolderNavList daoTool.get("folder").getFolderTree("/") />
</li>
</ul>
</div>







<#--
<#macro ooFolderNavList treeItem>
<#if treeItem.children?has_content>
<ul>
<#list treeItem.children as f><li class="expanded"><a href="${oo.url("/otherobjects/workbench/list/"+f.id)}">${oo.msg(f.label)}</a><@ooFolderNavList f/></li></#list>
</ul>
</#if>
</#macro>



<div class="oo-navigation">
<ul class="oo-folders" id="ooNavigation">
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
-->