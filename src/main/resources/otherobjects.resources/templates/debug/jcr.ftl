<#import "/oo.ftl" as oo/>

<style>
.live LI {color:green;}
.edit LI {color:red;}
</style>

<script type="text/javascript" src="http://cdn.othermedia.com/ojay-yui/2.4.1.js"></script>
<script type="text/javascript" src="http://cdn.othermedia.com/ojay/0.1.2/all-min.js"></script>

<h1>JCR Browser</h1>

<form method="get">
<p>
Path: <input style="width:300px;" type="text" name="path" value="${path!}"/>
and/or Type: <input style="width:300px;" type="text" name="type" value="${type!}"/>
</p>
<p><b>or</b> use custom query: <input style="width:500px;" type="text" name="xpath" value="${xpath!}"/></p>
<input name="submit" type="submit" value="Search"/>
<input name="export" type="submit" value="Export"/>
</form>

Live workspace:<br/>
<div class="live">${liveNodesHtml}</div>

Edit workspace: <br/>
<#if editNodesHtml??>
<div class="edit">${editNodesHtml}</div>
<#else>
<p>You are not an editor so you can not see the edit workspace.</p> 
</#if>
<script>
$('.properties-area').setStyle({display:'none'}).insert(' <a class="view" href="javascript:void;">[View]</a>', 'before');
$('.view').on('click').siblings().setStyle({display: 'block'});
</script>

<hr>

<p><a href="${oo.url("/otherobjects/debug/import")}">Import</a></p>



