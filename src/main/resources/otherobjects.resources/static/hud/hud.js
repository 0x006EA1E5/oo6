var hudVisible = false;
var siteMinWidth = 1016;
var siteBodyOverflow = "auto"; // FIXME This needs to be recorded before entering hud.
var bodyWidth = Ojay('body').setStyle({'width' : YAHOO.util.Dom.getViewportWidth()})

Ojay.Keyboard.listen(document, 'ESCAPE', toggleHud);
Ojay('div.oo-icon').on('click',toggleHud); 
				
function toggleHud(el, e) {
	if(hudVisible) {
		// Animate hud closed
		Ojay('div.oo-edit-zones').node.innerHTML = "";
		Ojay('.oo-toolbar').animate({top:{from: 0, to: -60}}, 0.5);
		Ojay('html').wait(0.1).animate({paddingTop:{from: 100, to: 0}, paddingBottom:{from: 200, to: 0}}, 0.5).removeClass('oo');
		Ojay('body').
			wait(0.1).
			setStyle({'overflowX' : 'hidden'}).
			animate({left: {to:0}, width: {to: YAHOO.util.Dom.getViewportWidth()}}, 0.5).
			setStyle({'overflowX' : siteBodyOverflow});
		hudVisible = false;
	} else {
		// Animate hud open
		Ojay('html').addClass('oo').animate({paddingTop:{from: 0, to: 100}, paddingBottom:{from: 0, to: 200}}, 0.75);
		Ojay('body').
			setStyle({'overflowX' : 'hidden'}).
			animate({left: {to: Math.floor((YAHOO.util.Dom.getViewportWidth()-siteMinWidth)/2)}, width: {from:YAHOO.util.Dom.getViewportWidth(), to: siteMinWidth}}, 0.75).
			setStyle({'overflowX' : siteBodyOverflow});
		Ojay('.oo-toolbar')
			.wait(0.3)
			.animate({top:{from: -60, to: 0},opacity:{from: 0, to: 1}}, 0.5)
			._('div.oo-block').forEach(function(el,i) {
				// Get Regions Dimensions
				var area = el.getRegion();
				
				// Create Edit Zone HTML
				var zoneHtml = Ojay.HTML.div({id: 'OoEditZone' + i , className: 'oo-edit-zone'});
				Ojay('div.oo-edit-zones').insert(zoneHtml,'top');
				
				// Set click handler
				Ojay('#OoEditZone'+i).on('click', function(el, e) {
				    e.stopDefault();
					//var overlay = $('.oo-menu').node;
					//ooBlockInEdit = el.getId();
					//alert(ooBlockInEdit);
					$('.oo-menu').setStyle({display:"block"});
					//Ojay.HTTP.GET('' + ooBaseUrl + 'otherobjects/block/form/'+blockReferenceId+'?resourceObjectId='+resourceObjectId).insertInto('#oo-form-overlay').evalScripts();
				});
				
				
				
				
				//  Create Edit Label HTML
				var labelHtml = Ojay.HTML.div({id: 'OoEditLabel' + i , className: 'oo-edit-label'}, function(HTML) {
					// Insert Actions Arrow
					HTML.div({id: 'OoEditActions' + i , className: 'oo-edit-label-actions'}, function(HTML) {
						// Edit Status Gem and Label Text
						HTML.div({className:'oo-text-style oo-edit-state oo-edit-state-' + el.node.getAttribute('editstate').toLowerCase(), title:'Status: ' + el.node.getAttribute('editstate') },el.node.getAttribute('editlabel'));
					});
				});
				Ojay('#OoEditZone' + i).insert(labelHtml,'top');
				
				Ojay('#OoEditZone' + i).setStyle({opacity: '0', top:area.top + 'px', left:area.left + 'px', width:(area.getWidth()-4) + 'px', height:(area.getHeight()-4) + 'px'}).animate({opacity:  {from: 0, to: 1}}, 0.5);
			});
		
		hudVisible = true;
	}
}


/*
('.oo-editregion').forEach(function(el,i) {
		// Loop create editregions
	var area = el.getRegion();
	var htmlIns = Ojay.HTML.div({id: 'editzone' + i , className: 'oo-editzone'});
	Ojay('div.oo-editzones').insert(htmlIns,'top');
		// Insert Drop item
		var htmlEditIns = Ojay.HTML.div({id: 'draghandle' + i , className: 'oo-draghandle oo-text-style'});
		Ojay('#editzone' + i).insert(htmlEditIns,'top');
		
	Ojay('#editzone' + i).setStyle({opacity: '0', top:area.top + 'px', left:area.left + 'px', width:(area.getWidth()-4) + 'px', height:(area.getHeight()-4) + 'px'}).animate({opacity:  {from: 0, to: 1}}, 0.5);
		Ojay('#draghandle' + i).setContent('<span>' + el.node.title + '</span>');
		
		var dd2 = new YAHOO.util.DD('editzone' + i);
		dd2.setHandleElId('draghandle' + i); 
})
*/
