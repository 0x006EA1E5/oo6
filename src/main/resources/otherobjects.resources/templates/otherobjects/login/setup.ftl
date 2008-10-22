<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#import "/forms.ftl" as forms />

<#assign pageTitle = "Setup" />

<#include "/otherobjects/templates/legacy/blocks/header.ftl" />

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
<td class="oo-label">Email address<span class="oo-required">*</span></td>
<td><p><@forms.formInput "command.user.email" /></p></td>
<td style="color:red; font-weight:normal!important;"><p><@forms.showErrors "<br>"/></p></td>
</tr>
<tr>
<tr>
<td class="oo-label">Email address<span class="oo-required">*</span></td>
<td><p><@forms.formInput "command.oldPassword" /></p></td>
<td style="color:red; font-weight:normal!important;"><p><@forms.showErrors "<br>"/></p></td>
</tr>
<tr>
<tr>
<td class="oo-label">Email address<span class="oo-required">*</span></td>
<td><p><@forms.formInput "command.newPassword" /></p></td>
<td style="color:red; font-weight:normal!important;"><p><@forms.showErrors "<br>"/></p></td>
</tr>
<tr>
<tr>
<td class="oo-label">Email address<span class="oo-required">*</span></td>
<td><p><@forms.formInput "command.user.email" /></p></td>
<td style="color:red; font-weight:normal!important;"><p><@forms.showErrors "<br>"/></p></td>
</tr>
<tr>
<tr>
<td class="oo-label">Email address<span class="oo-required">*</span></td>
<td><p><@forms.formTextarea "command.user.email" /></p></td>
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

<div class="oo-actions">
<h2>Actions</h2>
<ul>
<li><a href="javascript:formSubmit();">Save</a></li>
</ul>
</div>


<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />

