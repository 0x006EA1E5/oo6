<@oo.authorize ifAllGranted="ROLE_ADMIN">
<!-- Start HUD Elements -->

<script>
var resourceObjectId = '${(resourceObject.id)!}';
var ooTemplateId = '${(ooTemplate.id)!}';
var ooBaseUrl = '${oo.url("/")}';
var ooBlockInEdit = "";
</script>

<script type="text/javascript" src="http://cdn.othermedia.com/ojay-yui/2.5.1.js"></script>
<script type="text/javascript" src="http://cdn.othermedia.com/ojay/0.2.0/all-min.js"></script>
<@oo.js "/otherobjects/static/hud/hud.js"/>
<@oo.css "/otherobjects/static/hud/hud.css"/>

<div id="oo-hud">

<div id="oo-form-overlay" style="display:none;" class="oo-panel"></div>

<div>
<#include "/blocks/oo-hud.ftl">
</div>

</div>

<!-- End HUD Elements -->
</@oo.authorize>
