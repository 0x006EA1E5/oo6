<div class="oo-menu">
	<div class="oo-menu-cell" style="width:50%;"></div>
	<div class="oo-menu-cell">
	<div class="oo-panel oo-text-style" style="width:750px; height:500px;">

		<div class="oo-title"><big>Editing: What we do </big></div>
	
		<div class="oo-listing">
		  	<table style="width:750px;">
		  	<thead>
		  	<tr>
		  	<th colspan="2" width="750">What we do</th>
		  	</tr>
		  	</thead>
			
		  	
		  	<tbody>
		  	<tr class="oo-listing-field">
		  	<td width="200">Title</td>
		  	<td width="600"><textarea>What we do</textarea></td>
		  	</tr>
		
		  	<tr class="oo-listing-field oo-listing-arrow">
		  	<td width="200">SEO Data</td>
		  	<td width="650">
			Web design, online marketing, ecommerce, CMS | the OTHER media web design agency London the OTHER media offers a range of services...
			</td>
		  	</tr>
		
		  	<tr class="oo-listing-field">
		  	<td width="200">First Subtitle</td>
		  	<td width="650"><textarea onkeypress="document.getElementById('spanTtl').innerHTML = this.value" onkeyup="document.getElementById('spanTtl').innerHTML = this.value">What we do</textarea></td>
		  	</tr>
		
		  	<tr class="oo-listing-field oo-listing-arrow">
		  	<td width="200">First Image</td>
		  	<td width="650"><img src="icons/no-image.png" width="200" height="200"></td>
		  	</tr>
		
		  	<tr id="action4" class="oo-listing-field oo-listing-arrow">
		  	<td width="200">First Text Block</td>
		  	<td width="650">the OTHER media team is hand picked from a variety of specialities to give you all-round support: from design and build through to digital marketing...</td>
		  	</tr>
		
		  	<tr class="oo-listing-field">
		  	<td width="200">Second Subtitle</td>
		  	<td width="650"><textarea>How we do it</textarea></td>
		  	</tr>
		
		  	<tr class="oo-listing-field oo-listing-arrow">
		  	<td width="200">Second Image</td>
		  	<td width="650"><img src="icons/no-image.png" width="200" height="200"></td>
		  	</tr>
		
		  	<tr class="oo-listing-field oo-listing-arrow">
		  	<td width="200">Second Text Block</td>
		  	<td width="650">Together we identify your site objectives and audiences: developing personas to map user journeys and create intuitive information architectures (IA)...</td>
		  	</tr>
		  	
		  	<tr class="oo-listing-field oo-listing-arrow">
		  	<td width="200">Promo Blocks</td>
		  	<td width="650">
		  	<ul>
		  	<li>design and usability</li>
			<li>digital strategy</li>
			<li>online marketing</li>
			<li>content management</li>
			<li>ecommerce</li>
			<li>hosting</li>
		  	</ul>
		  	</td>
		  	</tr>
		
		  	<tr class="oo-listing-field oo-listing-arrow">
		  	<td width="200">Publishing Options</td>
		  	<td width="650">
		  	<ul>
		  	<li>Publish: Immediately</li>
			<li>Expires: Never</li>
			<li>Template: Default</li>
		  	</ul>
		  	</td>
		  	</tr>
		   	</tbody>
		  	<tfoot>
		  	<tr class="oo-listing-arrow">
			<td width="200">History</td>
		  	<td width="650">
			Last Saved 2mins ago by Chris H<br>
			Published 3 days ago by Chris H
			</td>
		  	</tr>
		  	</tfoot>
		  	</table>
		  </div>	
		
		  <div class="oo-actions" style="width:750px;">
			<div class="oo-action"><div class="oo-button oo-button-red oo-center-text">Save</div></div>
			<!--
			<div class="oo-action"><div class="oo-button oo-button-grey oo-center-text">Revert</div></div>
			<div class="oo-action"><div class="oo-button oo-button-green oo-center-text">Publish</div></div>
			 <div class="oo-action"><div class="oo-button oo-button-blue oo-center-text oo-arrow">More</div></div>
			-->
		</div>
	</div></div>
	<div class="oo-menu-cell" style="width:50%;"></div>
</div>





</div>

<div class="oo-edit-zones"></div>

<div class="oo-toolbar oo-text-style">
	<div class="oo-toolbar-icons">
		
		<div class="oo-toolbar-left">
			<div class="oo-toolbar-icon oo-dashboard-icon "><div class="oo-small-badge"><div class="oo-small-badge-label">2</div></div></div>
			<div class="oo-toolbar-icon oo-new-icon"></div>
			<div class="oo-toolbar-icon oo-commerce-icon"></div>
			<div class="oo-toolbar-icon oo-users-icon"></div>
		</div>
		
		<div class="oo-toolbar-center">
			<input class="oo-search-input" type="text">
		</div>
		
		<div class="oo-toolbar-right">
			<div class="oo-toolbar-radio">
				<div class="oo-toolbar-icon oo-edit-icon oo-icon-selected"></div>
				<div class="oo-toolbar-icon oo-stats-icon"></div>
				<div class="oo-toolbar-icon oo-design-icon"></div>
				<div class="oo-toolbar-icon oo-debug-icon"></div>
			</div>
			<div class="oo-toolbar-icon oo-logout-icon"></div>
		</div>
		
	</div>
</div>

<script>
Ojay('.oo-logout-icon').on('click', function() {
	location.href='${oo.url("/otherobjects/logout.html")}';
}); 
</script>