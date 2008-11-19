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

function addToList()
{
	// Get field
	var f = $('.oo-list-empty-field').at(0);
	

	
	// Insert copy
	n = $(f.node.cloneNode(true));
	var re = new RegExp("\\[\\d\\]", "g");
	
	// Insert new index 
	n.node.innerHTML = n.node.innerHTML.replace(re, "["+ nextIndex +"]");
	console.log(n);
	n.setStyle({display:'block'});
	n.removeClass("oo-list-template");
	f.insert(n.node, 'after');
	nextIndex++;
}

function removeFromList()
{
	// Get field
	var all = $('.oo-list-last-field');
	var f = all.at(all.length-1);
	// Insert copy
	f.parents().at(0).node.removeChild(f.node);
	nextIndex--;
}