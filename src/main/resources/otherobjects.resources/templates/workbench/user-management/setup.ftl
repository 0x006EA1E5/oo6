<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#import "/forms.ftl" as forms />

<#assign pageTitle = "Setup" />

<#include "/otherobjects/templates/workbench/shared/header.ftl" />

<div id="ooContent">
<div class="oo-content">

<h2>Configure admin user</h2>

<p>Before using OTHERobjects you should configure the admin user. Until you do this the admin
user password will be reset on each startup.</p>

<form id="oo-form" action="${oo.url("/otherobjects/setup")}" method="post">

<table class="oo-edit">
<thead>
<tr>
<th>Field</th>
<th>Value</th>
<th>Help</th>
</tr>
</thead>
<tbody>
<tr>
<td class="oo-label">Email address <span class="oo-required">*</span></td>
<td><p><@forms.formInput "command.email" /></p></td>
<td style="color:red; font-weight:normal!important;"><p><@forms.showErrors "<br>"/></p></td>
</tr>
<tr>
<tr>
<td class="oo-label">Old password <span class="oo-required">*</span></td>
<td><p><@forms.formPassword "command.oldPassword" /></p></td>
<td style="color:red; font-weight:normal!important;"><p><@forms.showErrors "<br>"/></p></td>
</tr>
<tr>
<tr>
<td class="oo-label">New password <span class="oo-required">*</span></td>
<td><p><@forms.formPassword "command.passwordChanger.newPassword" /></p></td>
<td style="color:red; font-weight:normal!important;"><p><@forms.showErrors "<br>"/></p></td>
</tr>
<tr>
<tr>
<td class="oo-label">Confirm new password <span class="oo-required">*</span></td>
<td><p><@forms.formPassword "command.passwordChanger.newPasswordRepeated" /></p></td>
<td style="color:red; font-weight:normal!important;"><p><@forms.showErrors "<br>"/></p></td>
</tr>
<tr>
<tr>
<td class="oo-label">Password hint</td>
<td><p><@forms.formTextarea "command.passwordHint" /></p></td>
<td style="color:red; font-weight:normal!important;"><p><@forms.showErrors "<br>"/></p></td>
</tr>
<tr>
</tbody>
</table>

</form>

<script type="text/javascript">
function formSubmit()
{
	$('#oo-form').node.submit();
}
</script>
</div>
</div>

<div id="ooActions" class="oo-panel oo-text-style">
<h2>Actions</h2>
<a class="oo-item" href="javascript:formSubmit();"><div class="oo-arrow">Save</div></a>
</div>


<#include "/otherobjects/templates/workbench/shared/footer.ftl" />

