// Define OO namespace
var OO = OO || {};

// Exm blank image
Ext.BLANK_IMAGE_URL = '/resources/static/ext-js/images/default/s.gif';

OO.Workbench = function()
{
    // a bunch of private variables accessible by member function
    var layout;
    
    return {
        init : function(){
            // Set up workbench layout
			layout = new Ext.BorderLayout(document.body, {
	            north: {split:false, initialSize: 25, minSize: 60, maxSize: 60, titlebar: false, collapsible: false, animate: false},	                    
				//east: {split:true, collapsed: false, hidden: true, initialSize: 350, minSize: 350, maxSize: 500, titlebar: true, collapsible: true, animate: false},
				west: {split:true, collapsed: false, initialSize: 200, minSize: 200, maxSize: 500, collapsible: true, animate: false, autoScroll:true},
	            center: {titlebar: false, autoScroll:true, closeOnTab: true}
	        });
	
	
			tabs = new Ext.TabPanel('content');
			tabs.addTab('listing-tab', "Listing");
			tabs.addTab('edit-tab', "Edit");
			tabs.getTab('edit-tab').disable();
			
			var tab2 = tabs.addTab('template-designer-tab', "Template Designer");
			var updater = tab2.getUpdateManager();
        	updater.setDefaultUrl('/go/workbench/designer.html');
			tab2.disable();
        	//tab2.on('activate', activateTemplateDesignerTab, tab2);

			var previewTab = tabs.addTab('preview-tab', "Preview");
			//previewTab.on('activate', activatePreviewTab, previewTab);
			previewTab.disable();
			
			var bulkUploadTab = tabs.addTab('bulk-upload-tab', "Bulk Upload");
			updater = bulkUploadTab.getUpdateManager();
        	updater.setDefaultUrl('/go/workbench/uploader.html');
        	//bulkUploadTab.on('activate', activateBulkUploaderTab, bulkUploadTab);
			
			tabs.activate('listing-tab');
	
	        layout.beginUpdate();
			layout.add('north', new Ext.ContentPanel('header'));
	        //layout.add('east', editPanel);
	        layout.add('west', new Ext.ContentPanel('select', {title: 'Select'}));
	        layout.add('center', new Ext.ContentPanel('content', {title: 'Center Panel', closable: false}));
	        layout.endUpdate();
        }
    };
}();
Ext.onReady(OO.Workbench.init, OO.Workbench);