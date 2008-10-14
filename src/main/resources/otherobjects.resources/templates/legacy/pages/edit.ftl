<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<#assign pageTitle = "Editing: ${oo.msg(object.label)}" />
<#include "/otherobjects/templates/legacy/blocks/header.ftl" />
<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">

<h2>
${pageTitle}
</h2>

<form action="${oo.url("/otherobjects/form")}" method="post">
<input type="hidden" name="editableId" value="${id}">

<@oo.renderForm typeDef />

</form>
</div>

<div class="oo-actions">
<h2>Actions</h2>
<ul>
<li><a href="javascript:document.forms[1].submit();">Save</a></li>
</ul>
</div>

<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />
