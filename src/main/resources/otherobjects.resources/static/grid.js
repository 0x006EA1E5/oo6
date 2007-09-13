//
// grid.js
//
// Defines OO.Grid providing a sortable grid of data.
//

OO.ListingGrid = function() {
	
	var cm, ds, grid, mappings;
	
	function renderState(value, p, record) {
		console.log("State: " + value)
		if(value.length==0)
			return '<span style="padding-left:15px; background:url(/resources/otherobjects.resources/static/icons/bullet-black.png) no-repeat -2px -2px">Database</span>';
		else if (value == true)
			return '<span style="padding-left:15px; background:url(/resources/otherobjects.resources/static/icons/bullet-green.png) no-repeat -2px -2px">Live</span>';
		else
			return '<span style="padding-left:15px; background:url(/resources/otherobjects.resources/static/icons/bullet-red.png) no-repeat -2px -2px">Edited</span>';
				
	}
	
	function renderDate(value, p, record) {
		// FIXME Temp hack. Need better data formatting class.
		function pad(v) { if(v.length==1) return "0"+v; else return "" + v;}
		var d = new Date();
		d.setTime(value)
		return pad(""+d.getHours()) + ":" + pad(""+d.getMinutes()) + " on " + d.dateFormat("d M Y");
	}
	
	function renderThumbnail(value, p, record) {
		// FIXME Temp hack. Need better data formatting class.
		function pad(v) { if(v.length==1) return "0"+v; else return "" + v;}
		var d = new Date();
		d.setTime(value)
		return '<img src="' + value + '" height="100" width="100">';
	}
	
	return {
	    init : function() {
			
			mappings = [
	            {name: 'id', mapping: 'id'},
	            {name: 'editableId', mapping: 'editableId'},
	            {name: 'label', mapping: 'label'},
	            {name: 'modificationTimestamp', mapping: 'modificationTimestamp'},
	            {name: 'ooType', mapping: 'ooType'},
	            {name: 'linkPath', mapping: 'linkPath'},
	            {name: 'published', mapping: 'published'},
	            {name: 'thumbnailPath', mapping: 'thumbnailPath'}
			];
			
			// create the Data Store
			ds = new Ext.data.Store({
			    // load using script tags for cross domain, if the data in on the same domain as
			    // this page, an HttpProxy would be better
			    proxy: new Ext.data.HttpProxy({
			        url: '/go/workbench/data/listing'
			    }),
			
			
			    // create reader that reads the Topic records
			    reader: new Ext.data.JsonReader({
			        root: 'items',
			        totalProperty: 'totalItems',
			        id: 'editableId'
			        }, mappings),
			
			    // turn on remote sorting
			    remoteSort: true
			});
			
			// create the column model
			cm = new Ext.grid.ColumnModel([
				{ header: 'State', width: 100, sortable: true, dataIndex: 'published', renderer:renderState },
				{ header: 'Label', width: 200, sortable: true, dataIndex: 'label' },
				{ header: 'Type', width: 300, sortable: true, dataIndex: 'ooType' },
				{ header: 'Last modified', width: 300, sortable: true, dataIndex: 'modificationTimestamp', renderer:renderDate },
				{ header: 'Path', width: 300, sortable: false, dataIndex: 'linkPath', hidden:true  },
				{ header: 'UUID', width: 300, sortable: false, dataIndex: 'id', hidden:true },
				{ header: 'Thumbnail', width: 300, sortable: false, dataIndex: 'thumbnailPath', hidden:true, renderer:renderThumbnail }
			]);
			
			// create the grid
			grid = new Ext.grid.Grid('listing-panel', {
			    ds:ds,
			    cm:cm,
				enableDrag:true,
				ddGroup : 'NavigatorDD',
			    selModel:new Ext.grid.RowSelectionModel({singleSelect:true}),
			    enableColLock:false,
			    loadMask:true
			});
			grid.on('rowclick', OO.Workbench.selectItem, this);
		    grid.on('rowdblclick', OO.Workbench.activateEditor, this);
			
			// render it
			grid.render();
			
			
			
			// Add paging toolbar
			var gridFoot = grid.getView().getFooterPanel(true);
			var paging = new Ext.PagingToolbar(gridFoot, ds, { 
				pageSize:25,
				displayInfo: true,
			    displayMsg: 'Displaying {0} - {1} of {2}',
			    emptyMsg: "No objects to display"
			});
			paging.add('-', 'Filter: '); // add a separator followed by a text string to label the filter input
			// add a DomHelper config to the toolbar and return a reference to it
			var filter = Ext.get(paging.addDom({tag: 'input', type:'text', size:'30', value: '', cls: 'x-grid-filter'}).el);
			filter.on('keypress', function(e) { // setup an onkeypress event handler
				if(e.getKey() == e.ENTER) // listen for the ENTER key
					ds.load({params: {start:0, limit:20}});
			});
			filter.on('keydown', function(e) { // setup an onkeyup event handler
				var key = e.getKey(); // assign the current key to a local variable
				if((key == e.BACKSPACE || key == e.DELETE) && this.getValue().length == 0) // listen for the BACKSPACE or DELETE keys and the field being empty
					ds.load({params: {start:0, limit:20}});
			});
			filter.on('focus', function(){filter.dom.select()}); // setup an onfocus event handler to cause the text in the field to be selected
			ds.on('beforeload', function() {
				ds.baseParams = {node:OO.Workbench.currentContainer, q:filter.getValue()};
			});
			
			
			// Add toolbar
			var addMenu = new Ext.menu.Menu({
		        id: 'mainMenu'
			});
			
			var publishButton = new Ext.Toolbar.MenuButton({
		            text: 'Publish',
		            handler: function(){OO.ListingGrid.publishSelected("");},
		            tooltip: {text:'This is a QuickTip with autoHide set to false and a title', title:'Tip Title', autoHide:false},
		            cls: 'x-btn-text-icon publish-btn',
					menu: {items: [{text: 'Publish with comment', handler:function(e){
				        Ext.MessageBox.show({
				           title: 'Publishing comment',
				           msg: 'Please describe the changes you have made:',
				           width:300,
				           buttons: Ext.MessageBox.OKCANCEL,
				           animEl:publishButton.el,
				           fn:function(btn, msg){OO.ListingGrid.publishSelected(msg)},
				           multiline: true
				       });
					}}]}	            
		        });
			
			var gridHeader = grid.getView().getHeaderPanel(true);
			var gridToolbar = new Ext.Toolbar(gridHeader, [
		  		{cls:'x-btn-text-icon add-btn', text:'Add object', menu:addMenu, handler:function(e){
					e.menu.removeAll();	
					var types = OO.Workbench.getCurrentNode().attributes.allAllowedTypes;
					if(types && types.length>0) {
						for(var i=0; i<types.length; i++) {
							var fn = function(ev) {
								OO.ListingGrid.addItem(ev.code, ev);
								};
							var item = new Ext.menu.Item({text: types[i].label, code:types[i].name});
							item.on("click", fn, this);
							e.menu.addItem(item);
						}
					}
					else {
						Ext.Msg.alert("Error", "This folder has not been configured with allowed types yet.");
						return false;
					}
				}},
				'-',
				publishButton,
				'-',
		  		{cls:'x-btn-text-icon refresh-btn', text:'Refresh', handler:function(e){OO.ListingGrid.refresh();}}
			]);
			
			return new Ext.GridPanel(grid, {title:'Listing',background:true});
	    },
	    
	    refresh : function() {
	      ds.load({params:{start:0, limit:25}});
	    },
		
	    load : function(uuid) {
		  // Set parameters to send in all data requests 
//	      ds.baseParams={node:uuid};
		  // FIXME Shouldn't need this in 2 places
		  OO.Workbench.currentContainer = uuid;
	      ds.load({params:{start:0, limit:25}});
	    },
		
		publishSelected : function(message)
		{
			var r = grid.getSelectionModel().getSelected();
			var id = r.data.id;
			console.log("Publishing record: " + id);
			ContentService.publishItem(id, message, function(item) { OO.ListingGrid.updateItem(item);});
		},
		
		updateItem : function(item)
		{
			console.log("Updating record id: " + item.id, item);
			var r = ds.getById(item.id);
			var ArticleRecord = Ext.data.Record.create(mappings);
			var newRecord = new ArticleRecord(item, item.id);
			r.data = newRecord.data;
			grid.getView().refresh();
		},
		
		addItem : function(type, e)
		{
			var c = OO.Workbench.currentContainer;
			console.log('Creating new item: ' + type + ' at ' + c);
			ContentService.createItem(c,type, function(item) {
				var ArticleRecord = Ext.data.Record.create(mappings);
				var myNewRecord = new ArticleRecord(item,item.id);
				console.log("Adding new item to grid",myNewRecord);
				ds.add(myNewRecord); 
				// FIXME Dont highlight unless truly selected 
				//grid.getSelectionModel().selectLastRow();
			});
		}
  	}
}();