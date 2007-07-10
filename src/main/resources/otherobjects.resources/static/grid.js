OO.ListingGrid = function() {
	
	var cm, ds, grid, panel;
	
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
			            {name: 'label', mapping: 'label'}
			        ]),
			
			    // turn on remote sorting
			    remoteSort: true
			});
			
			// create the column model
			cm = new Ext.grid.ColumnModel([
				{ header: 'Label', width: 200, sortable: true, dataIndex: 'label' },
				{ header: 'Type', width: 200, sortable: true, dataIndex: 'ooType' },
				{ header: 'Path', width: 200, sortable: true, dataIndex: 'jcrPath' },
				{ header: 'UUID', width: 200, sortable: true, dataIndex: 'id' }
			]);
			
			// create the grid
			grid = new Ext.grid.Grid('listing-grid', {
			    ds: ds,
			    cm: cm,
			    selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
			    enableColLock:false,
			    loadMask: true
			});
			
			
			// render it
			grid.render();
			
			
			var gp = new Ext.GridPanel(grid, {title:'Listing',background:true});
			panel = OO.Workbench.addPanel(gp);
			
			var gridFoot = grid.getView().getFooterPanel(true);
			
			// add a paging toolbar to the grid's footer
			var paging = new Ext.PagingToolbar(gridFoot, ds, {
			    pageSize: 25,
			    displayInfo: true,
			    displayMsg: 'Displaying topics {0} - {1} of {2}',
			    emptyMsg: "No topics to display"
			});
			// add the detailed view button
			//    paging.add('-', {
			//        pressed: true,
			//        enableToggle:true,
			//        text: 'Detailed View',
			//        cls: 'x-btn-text-icon details',
			//        toggleHandler: toggleDetails
			//    });
			
			
			var gridHeader = grid.getView().getHeaderPanel(true);
			var gridToolbar = new Ext.Toolbar(gridHeader,
				[ 
			  {  cls:'x-btn-text-icon',
			     
			     text: 'Add item'
			      
			   }
				]
			);
			
			// FIXME -- only when activated: trigger the data store load
			ds.load({params:{start:0, limit:25}});

	    },
	    
	    load : function(uuid) {
	      ds.load({params:{start:0, limit:10, node:uuid}});      
//		  panel.activate();
	    }
  	}
}();