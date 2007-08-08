//
// grid.js
//
// Defines OO.Grid providing a sortable grid of data.
//

OO.ListingGrid = function() {
	
	var cm, ds, grid, mappings;
	
	return {
	    init : function() {
			
			mappings = [
	            {name: 'id', mapping: 'id'},
	            {name: 'label', mapping: 'label'},
	            {name: 'ooType', mapping: 'ooType'},
	            {name: 'path', mapping: 'linkPath'},
	            {name: 'published', mapping: 'published'}
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
			        //root: 'topics',
			        //totalProperty: 'totalCount',
			        id: 'id'
			        }, mappings),
			
			    // turn on remote sorting
			    remoteSort: false
			});
			
			// create the column model
			cm = new Ext.grid.ColumnModel([
				{ header: 'Label', width: 200, sortable: true, dataIndex: 'label' },
				{ header: 'Type', width: 100, sortable: true, dataIndex: 'ooType' },
				{ header: 'Path', width: 300, sortable: true, dataIndex: 'path' },
				{ header: 'UUID', width: 300, sortable: false, dataIndex: 'id' },
				{ header: 'Published', width: 300, sortable: false, dataIndex: 'published' }
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
			
			// TODO m2 Add paging toolbar
//			var gridFoot = grid.getView().getFooterPanel(true);
//			var paging = new Ext.PagingToolbar(gridFoot, ds, {
//			    pageSize: 25,
//			    displayInfo: true,
//			    displayMsg: 'Displaying topics {0} - {1} of {2}',
//			    emptyMsg: "No topics to display"
//			});
			
			// Add toolbar
			var gridHeader = grid.getView().getHeaderPanel(true);
			var gridToolbar = new Ext.Toolbar(gridHeader, [
		  		{cls:'x-btn-text-icon add-btn', text:'Add Article', handler:function(e){OO.ListingGrid.addItem('Article', e);}},
				'-',
		  		{cls:'x-btn-text-icon publish-btn', text:'Publish', handler:function(e){OO.ListingGrid.publishSelected();}},
				'-',
		  		{cls:'x-btn-text-icon refresh-btn', text:'Refresh', handler:function(e){OO.ListingGrid.refresh();}}
			]);
			
			return new Ext.GridPanel(grid, {title:'Listing',background:true});
	    },
	    
	    refresh : function() {
	      ds.load({params:{node:OO.Workbench.currentContainer}});
	    },
		
	    load : function(uuid) {
	      ds.load({params:{node:uuid}});
		  // FIXME Shouldn't need this in 2 places
		  OO.Workbench.currentContainer = uuid;
	    },
		
		publishSelected : function()
		{
			var r = grid.getSelectionModel().getSelected();
			console.log("Publishing record: " + r.id);
			ContentService.publishItem(r.id);
		},
		
		updateItem : function(item)
		{
			console.log("Updating record id: " + item.id);
			var r = ds.getById(item.id);
			var ArticleRecord = Ext.data.Record.create(mappings);
			var newRecord = new ArticleRecord(item);
			//FIXME Is there a better way to do this?
			r.data = newRecord.data;
			grid.getView().refresh();
		},
		
		addItem : function(type, e)
		{
			var c = OO.Workbench.currentContainer;
			console.log('Creating new item: ' + type + ' at ' + c);
			ContentService.createItem(c,type, function(item) {
				var ArticleRecord = Ext.data.Record.create(mappings);
				var myNewRecord = new ArticleRecord(item);
				console.log("Adding new item to grid",myNewRecord);
				ds.add(myNewRecord); 
				// FIXME Dont highlight unless truly selected 
				//grid.getSelectionModel().selectLastRow();
			});
		}
  	}
}();