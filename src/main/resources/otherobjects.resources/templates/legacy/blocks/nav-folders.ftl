<div class="oo-navigation">
<ul class="oo-folders">
<#list dao.folder.folders as f>
<li class="oo-folder-empty"><span></span><a href="${oo.url("/otherobjects/workbench/list/${f.id}")}">${f.jcrPath}</a></li>
</#list>
</ul>
</div>
