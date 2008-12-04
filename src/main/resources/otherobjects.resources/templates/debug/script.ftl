<#import "/oo.ftl" as oo>

<h1>Groovy Script Runner</h1>

<p>Enter script here. Output currently shows return value or exception only. App context is available as 'app' from which
cou can get any bean via getBean()</p>

<form method="post">
<textarea style="width:600px; height:300px;" name="script">${script!}</textarea>

<p><small>eg return app.getBean("userDao")</small></p>
<p><small>Available: app (ApplicationContext), jcr (UniversalJcrDao), logger (HtmlLogger)</small></p>

<p><input type="submit" value="Run"/></p>
</form>

<#if log??>
<h2>Log</h2>
<p>
  ${log}
</p>
</#if>

<#if output??>
<h2>Output</h2>
<p>
  ${output}
</p>
</#if>

<#if exception??>
<h2>Error</h2>
<p style="color:red;">
	<@oo.renderException exception />
</p>
</#if>
