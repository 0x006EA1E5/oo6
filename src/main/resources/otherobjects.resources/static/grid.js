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
				{ header: 'UUID', width: 200, sortable: true, dataIndex: 'id' }
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
//		    grid.on('rowdblclick', this.editRecord, this);
			
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
			var gridToolbar = new Ext.Toolbar(gridHeader,
				[ 
			  		{cls:'x-btn-text-icon', text:'Add item' }
				]
			);
			
			return new Ext.GridPanel(grid, {title:'Listing',background:true});
	    },
	    
	    load : function(uuid) {
	      ds.load({params:{start:0, limit:10, node:uuid}});
	    }
  	}
}();