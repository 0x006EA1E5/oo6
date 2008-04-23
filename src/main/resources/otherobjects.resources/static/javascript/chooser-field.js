function test()
{
	alert("TEST");
}

Ext.form.OOChooserField = function(config){
    Ext.form.OOChooserField.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.OOChooserField, Ext.form.TriggerField,  {

	chooser : null,
	thumbnail : null,
	hiddenField : null,

	onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        this.choose();
	},
	
	
	insertImage : function(data){
		console.log(data.id);
    	this.setValue(data.id);
    	this.thumbnail.dom.src=data.thumbnailPath;
    },
    
    choose : function (){
    	if(!this.chooser){
    		this.chooser = new ImageChooser({
    			url:'/go/workbench/data/select/org.otherobjects.cms.model.CmsImage',
    			//url:'/go/workbench/data/image-services/flickr',
    			width:515, 
    			height:400
    		});
    		this.chooser.on("choose", this.insertImage, this);
    	}
    	this.chooser.show(this.thumbnail);
    },
    
    setValue : function(v){
    	if(!v) {
    		this.thumbnail.dom.src='/resources/otherobjects.resources/static/graphics/thumbnail-blank.gif';
		}
		else if(v.id) {
    		this.thumbnail.dom.src=v.thumbnailPath
    		v = v.id;
    	}
        Ext.form.OOChooserField.superclass.setValue.call(this, v);
        this.value = v;
    },
    
     onRender : function(ct, position){
       	this.el = ct.createChild( {tag:'input', type:'hidden', name: this.name, id: this.name}, position);
        this.thumbnail = ct.createChild({tag:'img', src:'/resources/otherobjects.resources/static/graphics/thumbnail-blank.gif', style:"border:1px solid #999999;"}, position);
      	var actions = ct.createChild( {tag:'div'}, position);
		var button = new Ext.Button(actions, {text:'Unselect image'});
		button.on("click",function(e){
			this.setValue(null);
		}, this);
       	this.thumbnail.on("click", this.onTriggerClick, this);
    }
	
});


/*
 * Ext JS Library 1.0.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */
var ImageChooser = function(config){
	ImageChooser.superclass.constructor.call(this, config);
	
	this.addEvents({ choose : true });
	
	
    // create the dialog from scratch
    var dlg = new Ext.LayoutDialog(config.id || Ext.id(), {
		autoCreate : true,
		minWidth:400,
		minHeight:300,
		syncHeightBeforeShow: true,
		shadow:true,
        fixedcenter:true,
        center:{autoScroll:false}
		//east:{split:true,initialSize:150,minSize:150,maxSize:250}
	});
	dlg.setTitle('Choose an Image');
	dlg.getEl().addClass('ychooser-dlg');
	dlg.addKeyListener(27, dlg.hide, dlg);
    
    // add some buttons
    dlg.setDefaultButton(dlg.addButton('Cancel', dlg.hide, dlg));
    this.ok = dlg.addButton('Select', this.doCallback, this);
    this.ok.disable();
    dlg.on('show', this.load, this);
	this.dlg = dlg;
	var layout = dlg.getLayout();
	
	// filter/sorting toolbar
	this.tb = new Ext.Toolbar(this.dlg.body.createChild({tag:'div'}));
	this.sortSelect = Ext.DomHelper.append(this.dlg.body.dom, {
		tag:'select', children: [
			{tag: 'option', value:'name', selected: 'true', html:'Name'},
			{tag: 'option', value:'size', html:'File Size'},
			{tag: 'option', value:'lastmod', html:'Last Modified'}
		]
	}, true);
	this.sortSelect.on('change', this.sortImages, this, true);
	
	this.txtFilter = Ext.DomHelper.append(this.dlg.body.dom, {
		tag:'input', type:'text', size:'12'}, true);
		
	this.txtFilter.on('focus', function(){this.dom.select();});
	this.txtFilter.on('keyup', this.filter, this, {buffer:500});
	
	this.tb.add('Filter:', this.txtFilter.dom, '|');//;, 'Sort By:', this.sortSelect.dom);
	var iscButton = this.tb.addButton({text:'Add from flickr'});
    iscButton.on("click",function(e){this.createImageServiceChooser(e, this)}, this);
	
	var refreshButton = this.tb.addButton({text:'Refresh'});
    refreshButton.on("click",this.load, this);
	
	// add the panels to the layout
	layout.beginUpdate();
	var vp = layout.add('center', new Ext.ContentPanel(Ext.id(), {
		autoCreate : true,
		toolbar: this.tb,
		fitToFrame:true
	}));
	//var dp = layout.add('east', new Ext.ContentPanel(Ext.id(), {
	//	autoCreate : true,
	//	fitToFrame:true
	//}));
    layout.endUpdate();
	
	var bodyEl = vp.getEl();
	bodyEl.appendChild(this.tb.getEl());
	var viewBody = bodyEl.createChild({tag:'div', cls:'ychooser-view'});
	vp.resizeEl = viewBody;
	
	//this.detailEl = dp.getEl();
	
	// create the required templates
	this.thumbTemplate = new Ext.Template(
		'<div class="thumb-wrap" id="{id}">' +
		'<div class="thumb"><img src="{thumbnailPath}" title="{name}"></div>' +
		'<span>{name}</span></div>'
	);
	this.thumbTemplate.compile();	
	
	this.detailsTemplate = new Ext.Template(
		'<div class="details"><img src="{url}"><div class="details-info">' +
		'<b>Image Name:</b>' +
		'<span>{name}</span>' +
		'<b>Size:</b>' +
		'<span>{sizeString}</span>' +
		'<b>Last Modified:</b>' +
		'<span>{dateString}</span></div></div>'
	);
	this.detailsTemplate.compile();	
    
    // initialize the View		
	this.view = new Ext.JsonView(viewBody, this.thumbTemplate, {
		singleSelect: true,
		//jsonRoot: 'data',
		emptyText : '<div style="padding:10px;">No images match the specified filter</div>'
	});
    this.view.on('selectionchange', this.selectImage, this, {buffer:100});
    this.view.on('dblclick', this.doCallback, this);
    this.view.on('loadexception', this.onLoadException, this);
    this.view.on('beforeselect', function(view){
        return view.getCount() > 0;
    });
    Ext.apply(this, config, {
        width: 540, height: 400
    });
    
    var formatSize = function(size){
        if(size < 1024) {
            return size + " bytes";
        } else {
            return (Math.round(((size*10) / 1024))/10) + " KB";
        }
    };
    
    // cache data by image name for easy lookup
    var lookup = {};
    // make some values pretty for display
    this.view.prepareData = function(data){
		console.log("Prepare: " +data.originalFileId);
    	data.shortName = data.name;
    	data.sizeString = formatSize(data.size);
    	data.dateString = new Date(data.lastmod).format("m/d/Y g:i a");
		//data.thumbnailPath = '/data' + data.originalFileId.replace("originals","100x100%23DDDDDD");
    	lookup[data.id] = data;
    	return data;
    };
    this.lookup = lookup;
    
	dlg.resizeTo(this.width, this.height);
	this.loaded = false;
};

Ext.extend(ImageChooser, Ext.util.Observable,  {

	createImageServiceChooser : function(be, parentChooser) {
		var isc = new ImageChooser({
			//url:'/go/workbench/data/select/org.otherobjects.cms.model.CmsImage',
			url:'/go/workbench/data/image-services/flickr',
			width:515, 
			height:400
		});
		isc.on("choose", parentChooser.saveImage, parentChooser);
    	isc.show(be.el);
	},

	saveImage : function(data){
		console.log("Creating image: " + data.id);
		var o = this;
		ContentService.createImage("FLICKR",data.id, function(e){o.load()});
	},
	
	show : function(el, callback){
	    this.reset();
	    this.dlg.show(el);
		this.callback = callback;
	},
	
	reset : function(){
	    this.view.getEl().dom.scrollTop = 0;
	    this.view.clearFilter();
		this.txtFilter.dom.value = '';
		this.view.select(0);
	},
	
	load : function(){
		//if(!this.loaded){
			this.view.load({url: this.url, params:this.params, callback:this.onLoad.createDelegate(this)});
		//}
	},
	
	onLoadException : function(v,o){
	    this.view.getEl().update('<div style="padding:10px;">Error loading images.</div>'); 
	},
	
	filter : function(){
		var filter = this.txtFilter.dom.value;
		this.view.filter('name', filter);
		this.view.select(0);
	},
	
	onLoad : function(){
		this.loaded = true;
		this.view.select(0);
	},
	
	sortImages : function(){
		var p = this.sortSelect.dom.value;
    	this.view.sort(p, p != 'name' ? 'desc' : 'asc');
    	this.view.select(0);
    },
	
	selectImage : function(view, nodes){
	    var selNode = nodes[0];
		if(selNode && this.view.getCount() > 0){
			this.ok.enable();
		    var data = this.lookup[selNode.id];
            //this.detailEl.hide();
            //this.detailsTemplate.overwrite(this.detailEl, data);
            //this.detailEl.slideIn('l', {stopFx:true,duration:.2});
			
		}else{
		    this.ok.disable();
		    //this.detailEl.update('');
		}
	},
	
	doCallback : function(){
        var selNode = this.view.getSelectedNodes()[0];
		//var callback = this.callback;
		var lookup = this.lookup;
            if(selNode){
				var data = lookup[selNode.id];
				this.fireEvent("choose", data);
			}
		this.dlg.hide();
	}
});