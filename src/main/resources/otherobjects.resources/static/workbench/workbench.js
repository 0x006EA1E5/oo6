function ooStoreCaretPosition(txtArea) 
{
	if (txtArea.createTextRange)
	{
		txtArea.caretPos = document.selection.createRange().duplicate();
	}
}

function ooActivateWikiArea(textArea)
{
	var viewport = getViewportSize(), scrolls = getScrollOffsets();
	var viewRegion = new YAHOO.util.Region(scrolls.top, scrolls.left + viewport.width, scrolls.top + viewport.height, scrolls.left);
	var textareaRegion = YAHOO.util.Dom.getRegion(textArea);
	textareaRegion.bottom += 290;

	if(textArea.parentNode.className != 'oo-format-large') {
		textArea.parentNode.className = 'oo-format-large';
		var tools = Element.getElementsByClassName(textArea.parentNode, 'oo-edit-tools')[0];		
		// Check to see if scrolling is enabled, and if so, execute an animated scroll down to the wiki toolbar
		if ( viewRegion.contains(textareaRegion) ) {
			return;
		} else if ( /\bscrollto\b/.test(tools.className) && /\btop\b/.test(tools.className) ) {
			var htmlTag = document.getElementsByTagName('html')[0];
			var textHeight = Element.getHeight(textArea) + 10;
			var newYpos = Position.cumulativeOffset(tools)[1] - textHeight;
			var myAnim = new YAHOO.util.Scroll(htmlTag, { scroll: { to: [0, newYpos] } }, 0.5, YAHOO.util.Easing.easeOut);
			myAnim.animate();
		
		}
	}
}

function getViewportSize() {
	return {
		width: YAHOO.util.Dom.getViewportWidth(),
		height: YAHOO.util.Dom.getViewportHeight()
	};
}

function getScrollOffsets() {
	return {
		left: YAHOO.util.Dom.getDocumentScrollLeft(),
		top: YAHOO.util.Dom.getDocumentScrollTop()
	};
}

function ooFormating(txtArea,startTag,endTag) 
{
	txtArea = document.getElementById(txtArea);
	scrollPos = txtArea.scrollTop;
	if (txtArea.createTextRange && txtArea.caretPos) 
	{
		var caretPos = txtArea.caretPos;
		caretPos.text = caretPos.text.charAt(caretPos.text.length - 1) == " " ? startTag + caretPos.text + endTag : startTag + caretPos.text + endTag;
	}
	else if(txtArea.selectionStart >= 0)
	{
		txtArea.value = txtArea.value.substring(0,txtArea.selectionStart) + startTag + txtArea.value.substring(txtArea.selectionStart,txtArea.selectionEnd) + endTag + txtArea.value.substring(txtArea.selectionEnd,txtArea.value.length);
	}
	else
	{	
		window.prompt("Paste this text:", startTag + "your text here" + endTag);
	}
	txtArea.scrollTop = scrollPos;
}

function ooWordConvert(txtArea)
{
	console.log(" ooWordConv " + txtArea) ; 
    var str = document.getElementById(txtArea).value;
    str = str.replace(/(^|\n)[o??]\t/g, "\n* "); // Bullet Points
    str = str.replace(/(^|\n)\d\d?\.\t/g, "\n# "); // Numeric Points
    str = str.replace(/\r/g, ""); // Remove carrige returns
    
    str = str.replace(/(^|\n)(.*?\t.*?)(?=(\n|$))/g, "$1==========\n$2 \n=========="); // Table Rows
	str = str.replace(/(\t(\s{2,}|\s+(?=={10})))/g, ""); // Remove Empty Table Cells
	str = str.replace(/\t/g, "\n----------\n"); // Table Cells
	str = str.replace(/={10}(?=\s={10})/g, ""); // Remove Repeated Row dividers    
	str = str.replace(/(\w)(={10})/g, "$1\n$2"); // Add carrige returns at end of row
	str = str.replace(/ (?=\n={10})/g, ""); // Remove excess spaces
    
    
    document.getElementById(txtArea).value = str;
}

function ooSlideGrid(obj,width,height)
{
	document.getElementById("tabler-grid-" + obj).style.backgroundPosition = ((width * 30) - 150) + "px " + ((height * 30) - 120) + "px";
	document.getElementById("tabler-text-" + obj).innerHTML = "Table Size: " + width + " x " + height;
}

function ooShowHideGrid(obj)
{
	console.log(" ooShowHideGrid " + obj) ;
	if(document.getElementById("tabler-panel-" + obj).style.display == "inline")
	{
		document.getElementById("tabler-button-" + obj).style.backgroundColor = "";
		document.getElementById("tabler-panel-" + obj).style.display = "none";
		document.getElementById("tabler-grid-" + obj).style.backgroundPosition = "-150px -120px";
	}
	else
	{
		document.getElementById("tabler-text-" + obj).innerHTML = "Table Size: 0 x 0";
		document.getElementById("tabler-button-" + obj).style.backgroundColor = "#999999";
		document.getElementById("tabler-panel-" + obj).style.display = "inline";
	}
}

function ooFormating(txtArea,startTag,endTag) 
{
	txtArea = document.getElementById(txtArea);
	scrollPos = txtArea.scrollTop;
	if (txtArea.createTextRange && txtArea.caretPos) 
	{
		var caretPos = txtArea.caretPos;
		caretPos.text = caretPos.text.charAt(caretPos.text.length - 1) == " " ? startTag + caretPos.text + endTag : startTag + caretPos.text + endTag;
	}
	else if(txtArea.selectionStart >= 0)
	{
		txtArea.value = txtArea.value.substring(0,txtArea.selectionStart) + startTag + txtArea.value.substring(txtArea.selectionStart,txtArea.selectionEnd) + endTag + txtArea.value.substring(txtArea.selectionEnd,txtArea.value.length);
	}
	else
	{	
		window.prompt("Paste this text:", startTag + "your text here" + endTag);
	}
	txtArea.scrollTop = scrollPos;
}

function ooMakeTable(obj,width,height)
{
	var tableStr = "\n\n==========\n";
	
	if(document.getElementById("tabler-caption-" + obj).checked)
	{
		tableStr += "Table Caption\n==========\n";
	}
	
	if(document.getElementById("tabler-headings-" + obj).checked)
	{
		for(i=1; i<width; i++)
		{
			tableStr += "Heading " + i + "\n++++++++++\n";
		}
		tableStr += "Heading " + i + "\n==========\n";
	}
	
	for(i=1; i<=height; i++)
	{
		for(j=1; j<width; j++)
		{
			tableStr += "Row " + i + " Cell " + j + "\n----------\n";
		}
		tableStr += "Row " + i + " Cell " + j + "\n==========\n";
	}
	
	ooShowHideGrid(obj);
	ooFormating(obj,tableStr,"");
}



function formatDate(d)
{
	return d.getFullYear() + '-' + pad(d.getMonth()+1) + '-' + pad(d.getDate());
}

function formatTime(d)
{
	return pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds());
}

function formatTimestamp(d)
{
	return formatDate(d) + ' ' + formatTime(d);
}

function pad(v)
{
	if(v<10)
		return "0"+v;
	return v;
}

var fieldIndex = {};

function addToList(el, path)
{
	//console.log(el);
	//console.log(Ojay(el).ancestors('.oo-field').descendants());
	
	var nextIndex = fieldIndex[path];
	
	// Get field
	var f = Ojay(el).ancestors('.oo-field').descendants('.oo-list-empty-field').at(0);
	
	// Insert copy
	n = $(f.node.cloneNode(true));
	var re = new RegExp("\\[\\d\\]", "g");
	
	// Insert new index 
	n.node.innerHTML = n.node.innerHTML.replace(re, "["+ nextIndex +"]");
	console.log(n);
	n.setStyle({display:'block'});
	n.addClass("oo-list-last-field");
	n.removeClass("oo-list-template");
	f.insert(n.node, 'after');
	fieldIndex[path] = fieldIndex[path]+1;
}

function removeFromList(el, path)
{
	// Get field
	var all = Ojay(el).ancestors('.oo-field').descendants('.oo-list-last-field');
	var f = all.at(all.length-1);
	// Remove
	f.parents().at(0).node.removeChild(f.node);
	fieldIndex[path] = fieldIndex[path]-1;
}

function ooPopup()
{
	var w = window.screen.availWidth-100;
	if(w > 1000)
		w = 1000;
	var h = window.screen.availHeight-200;
	var t = 50;
	var l = 50;
	var popup = window.open("about:blank","ooPopup", "height="+h+", width="+w+", top="+t+", left="+l+", status=yes ,toolbar=yes ,menubar=no, location=no, resizable=yes, scrollbars=yes");
	popup.focus();
}

function ooInsertAtCaret(txtArea, text) 
{	
	if(txtArea.selectionStart >= 0) // Firefox, Safari
	{
		// Keep cursor position -- it gets reset when text area is changed
		var originalSelectionStart = txtArea.selectionStart;
		var originalSelectionEnd = txtArea.selectionEnd;
		
		var originalSelectionText = txtArea.value.substr(originalSelectionStart, originalSelectionEnd-originalSelectionStart);
		
		
		if(originalSelectionText.length > 0)
		{
			originalSelectionText = "|CAPTION:" + originalSelectionText + "]";
			text = text.replace("]",originalSelectionText);
		}
		
		// Update text area value
		txtArea.value = txtArea.value.substr(0,txtArea.selectionStart) + text  + txtArea.value.substr(txtArea.selectionEnd,txtArea.value.length);
		
		 
		// Highlight newly inserted text
		txtArea.selectionStart = originalSelectionStart;
		txtArea.selectionEnd =  originalSelectionStart + text.length;
	}
	else if (txtArea.createTextRange && txtArea.caretPos) // IE
	{
		var caretPos = txtArea.caretPos;
		
		var originalSelectionText = caretPos.text;
		if(originalSelectionText.length > 0)
		{
			originalSelectionText = "|CAPTION:" + originalSelectionText + "]";
			text = text.replace("]",originalSelectionText);
		}
				
        caretPos.text = text;
		caretPos.moveStart('character', - text.length);
		caretPos.select();
	}
	else
	{	
		window.prompt("Paste this into your text area:",text);
	}
	
}

function ooStoreCaretPosition(txtArea) 
{
	if (txtArea.createTextRange)
	{
		txtArea.caretPos = document.selection.createRange().duplicate();
	}
}


function ooPopupInlineSelect(fieldName, tag)
{	
	var textArea = document.getElementById(fieldName);
	
	if(textArea!=null)
	{
		scrollPos = textArea.scrollTop;
		ooInsertAtCaret(textArea,tag);
		textArea.focus();
		textArea.scrollTop = scrollPos;
	}
	else
	{
		location.reload();
	}
}

function ooPopupSelect(fieldName, objectId, objectLabel, objectIcon, objectImage)
{
	// Update hidden field
	document.getElementById(fieldName).value=objectId;
	
	// Update label/image/icon
	if(document.getElementById(fieldName+":LABEL"))
		document.getElementById(fieldName+":LABEL").innerHTML=objectLabel;
	if(document.getElementById(fieldName+":ICON"))
		document.getElementById(fieldName+":ICON").src=objectIcon;
	if(document.getElementById(fieldName+":IMAGE"))
		document.getElementById(fieldName+":IMAGE").src=objectImage;
}

function ooPopupRemove(fieldName)
{
	// Update hidden field
	document.getElementById(fieldName).value='';
	
	// Update label/image/icon paragraph
	if(document.getElementById(fieldName+":LABEL"))
		document.getElementById(fieldName+":LABEL").innerHTML='<em>Nothing selected</em>';
	if(document.getElementById(fieldName+":ICON")) 
		document.getElementById(fieldName+":ICON").src='/static/otherobjects/graphics/icon-blank.gif';
	if(document.getElementById(fieldName+":IMAGE"))
		document.getElementById(fieldName+":IMAGE").src='/classpath/otherobjects.resources/static/workbench/assets/thumbnail-blank.png'; // FIXME Add in servlet context
}

function ooEditWithPicnik(id, url)
{
	var currentUrl = window.location.toString();
	var baseUrl = currentUrl.substring(0,currentUrl.indexOf('/',10));
	
	url = encodeURIComponent(baseUrl + url);
	var exportUrl = encodeURIComponent(baseUrl + "/otherobjects/image/import");
	var closeUrl = encodeURIComponent(currentUrl);
	var location = "http://www.picnik.com/service/?id=" + id 
		+ "&_export="+exportUrl
		+ "&_close_target="+closeUrl
		+ "&_export_agent=browser&_export_title=Send to OTHERobjects&_host_name=OTHERobjects&_import=img&_exclude=in,out,create&img="+url
		+ "&_apikey=8858ee2c86bbd3a0d26fcb4e57b144b6";
	
	window.location = location;
}

function ooShowTip(obj, text)
{
	ooOldToolTip = obj.innerHTML;
	text = text.replace(". ", ".</strong><br /><br />");
	text = text.replace(/\.\s/g, ".<br /><br />");
	obj.innerHTML = obj.innerHTML + '<SPAN class="oo-help-tip"><strong>' + text + '</SPAN>';
}

function ooHideTip(obj)
{
	obj.innerHTML = ooOldToolTip;
}