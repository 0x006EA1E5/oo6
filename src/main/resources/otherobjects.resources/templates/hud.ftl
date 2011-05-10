<#import "/oo.ftl" as oo />

<#--
OO Interface Integration macros
-->

<#-- 
Macro to insert OO interface headers
-->
<#macro head>
<@oo.authorize "ROLE_ADMIN"> 
<@oo.js "/otherobjects/static/hud/hud.js" />
<@oo.css "/otherobjects/static/hud/hud.css" />
<@oo.css "/otherobjects/static/workbench/toolbar.css" />
</@oo.authorize>
</#macro>

<#-- 
Macro to insert toolbar code
-->
<#macro toolbar>
<@oo.authorize "" "ROLE_ADMIN,ROLE_EDITOR"> 
<#include "/otherobjects/templates/hud/toolbar.ftl" />
</@oo.authorize>
</#macro>
