<#import "/oo.ftl" as oo>

<h1>XML Import</h1>

<form method="post" accept-charset="UTF-8">
<textarea style="width:600px; height:300px;" name="xml">${xml!}</textarea>
<p><input type="submit" value="Import"/></p>
</form>

<#if log??>
<h2>Log</h2>
<p>
  ${log}
</p>
</#if>

<#if exception??>
<h2>Error</h2>
<p style="color:red;">
	<@oo.renderException exception />
</p>
</#if>
