var hudVisible = false;
var siteMinWidth = 1016;
var siteBodyOverflow = "auto"; // FIXME This needs to be recorded before entering hud.
var bodyWidth = Ojay('body').setStyle({'width' : YAHOO.util.Dom.getViewportWidth()})

Ojay('div.oo-icon').on('click', function (el, e) {
	if(hudVisible) {
		// Animate hud closed
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
		Ojay('.oo-toolbar').wait(0.3).animate({top:{from: -60, to: 0},opacity:{from: 0, to: 1}}, 0.5);
		
		hudVisible = true;
	}
});

/*
Ojay('html').addClass('oo').animate({paddingTop:{from: 0, to: 100}, paddingBottom:{from: 0, to: 200}}, 0.75);

Ojay('body').
	setStyle({'overflowX' : 'hidden'}).
	animate({left: {to: Math.floor((YAHOO.util.Dom.getViewportWidth()-siteMinWidth)/2)}, width: {from:YAHOO.util.Dom.getViewportWidth(), to: siteMinWidth}}, 0.75).
	setStyle({'overflowX' : siteBodyOverflow});
hudVisible = true;
*/