//
// grid.js
//
// Defines OO.Grid providing a sortable grid of data.
//

OO.ListingGrid = function() {
	
	var cm, ds, grid;
	
	return {
	    init : function() {
			
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
			        }, [
			            {name: 'id', mapping: 'id'},
			            {name: 'label', mapping: 'label'},
			            {name: 'ooType', mapping: 'ooType'},
			            {name: 'path', mapping: 'linkPath'}
			        ]),
			
			    // turn on remote sorting
			    remoteSort: true
			});
			
			// create the column model
			cm = new Ext.grid.ColumnModel([
				{ header: 'Label', width: 200, sortable: true, dataIndex: 'label' },
				{ header: 'Type', width: 200, sortable: true, dataIndex: 'ooType' },
				{ header: 'Path', width: 200, sortable: true, dataIndex: 'path' },
				{ header: 'UUID', width: 300, sortable: true, dataIndex: 'id' }
			]);
			
			// create the grid
			grid = new Ext.grid.Grid('listing-panel', {
			    ds: ds,
			    cm: cm,
			    selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
			    enableColLock:false,
			    loadMask: true
			});
			grid.on('rowclick', OO.Workbench.selectItem, this);
		    grid.on('rowdblclick', OO.Workbench.activateEditor, this);
			
			// render it
			grid.render();
			
			// Add paging toolbar
			var gridFoot = grid.getView().getFooterPanel(true);
			var paging = new Ext.PagingToolbar(gridFoot, ds, {
			    pageSize: 25,
			    displayInfo: true,
			    displayMsg: 'Displaying topics {0} - {1} of {2}',
			    emptyMsg: "No topics to display"
			});
			
			// Add toolbar
			var gridHeader = grid.getView().getHeaderPanel(true);
			var gridToolbar = new Ext.Toolbar(gridHeader, [
		  		{cls:'x-btn-text-icon add-btn', text:'Add Article', handler:function(e){OO.ListingGrid.addItem('Article', e);}},
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
		
		addItem : function(type, e)
		{
			var c = OO.Workbench.currentContainer;
			console.log('Creating new item: ' + type + ' at ' + c);
			ContentService.createItem(c,type);
		}
  	}
}();