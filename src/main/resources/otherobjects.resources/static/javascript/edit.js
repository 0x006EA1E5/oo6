//
// edit.js
//
// Defines OO.EditForm providing a dynamic data editor.
//

var OO = OO || {};

OO.EditForm = function(){

	var form;
	var dataId;
    var newDataType;

     return {
		
		showFailureMessage: function(form, scope)
		{
	      	Ext.Msg.alert('Warning', 'An error ocured whilst saving. Please try again.');
		},
		
		buildField : function(propDef, prefix, hiddenFields, doNotAdd) {
		
			var fName = '' + propDef.name;
			console.log("Creating field: " + fName + " type: " + propDef.type);
			
			if(propDef.type=="component") {
				if(!doNotAdd)
					form.fieldset({legend:propDef.label});
				var fields =  OO.EditForm.buildFormPart(propDef.relatedTypeDef, propDef.name+".", hiddenFields, doNotAdd);
				if(!doNotAdd) {
					form.end();
				}
				else
					return fields;
			}
			else if(propDef.type=="list") {
				var f = new Ext.form.OOListField(form, propDef, {fieldLabel:'Actions', label:propDef.label, id:prefix+propDef.name, name:prefix+propDef.name, width:'200px', allowBlank:true, disabled:false});
				return f;
			}
			else {
				var config = {fieldLabel:propDef.label, name: prefix+propDef.name, allowBlank:true};
				if(propDef.type=="date")
				{
					config.format='d/m/y';
					var f = new Ext.form.OODateField(config);
					f.on("change", function(f,n,o){console.log("Change",n);});
				}
				else if(propDef.type=="html")
				{
					function btn(id, f, handler){
			            return {
			                id : id,
			                cls : 'x-btn-icon',
			                enableToggle:false,
			                scope: f,
			                handler:handler,
							icon: '/resources/otherobjects.resources/static/icons/add.png',
			                clickEvent:'mousedown',
			                //tooltip: editor.buttonTips[id] || undefined,
			                tabIndex:-1
			            };
			        }
					

					
					//TODO Replace with WYMeditor?
					config.width=null; // FIXME Ext bug: can't set width of HtmlEditor
					// config.growMax=500;// FIXME Ext bug: can't grow HtmlEditor
					var f = new Ext.form.HtmlEditor(config);
					f.on("initialize", function(ed) {
						ed.getToolbar().addButton(btn('undersline',f, function() {
						var chooser = new ImageChooser({
				    			url:'/go/workbench/data/select/org.otherobjects.cms.model.CmsImage',
				    			//url:'/go/workbench/data/image-services/flickr',
				    			width:515, 
				    			height:400
    					});
						chooser.show();
    					chooser.on("choose", function(data){
							console.log(data.id);
    						//this.setValue(data.id);
    						//this.thumbnail.dom.src=data.thumbnailPath;
							this.execCmd('InsertImage', data.thumbnailPath);
						}, this);
					}
						));
					});
				}
				else if(propDef.type=="text")
				{
					config.width='500px';
					config.grow=true;
					config.growMax=500;
					var f = new Ext.form.TextArea(config);
				}
				else if(propDef.type=="boolean")
				{
					var f = new Ext.form.Checkbox(config);
					hiddenFields["_"+propDef.name] = "";
				}
				else if(propDef.type=="reference" && propDef.relatedType=="org.otherobjects.cms.model.CmsImage")
				{
					var f = new Ext.form.OOChooserField({
						
						fieldLabel: propDef.label,
			            name: propDef.name,
			            hiddenName: propDef.name, 
			            allowBlank:true,
					    //store: store,
					    displayField:'label',
					    valueField:'id',
					    typeAhead: true,
					    //mode: 'local',
					    triggerAction: 'all',
					    emptyText:'',
					    valueNotFoundText: '???',
					    selectOnFocus:true
					});
				}
				else if(propDef.type=="reference")
				{
				    var store = new Ext.data.Store({
				        proxy: new Ext.data.HttpProxy({
				            url: '/otherobjects/data/select/'+propDef.relatedType
				        }),
				
				        // create reader that reads the records
				        reader: new Ext.data.JsonReader({
				            id: 'id'
				        }, [
				            {name: 'id', mapping: 'id'},
				            {name: 'label', mapping: 'label'}
				        ])
				    });
					var f = new Ext.form.OOComboBox({
						
						fieldLabel: propDef.label,
			            name: propDef.name+":label",
			            hiddenName: propDef.name, 
			            allowBlank:true,
					    store: store,
					    displayField:'label',
					    valueField:'id',
					    typeAhead: true,
					    //mode: 'local',
					    triggerAction: 'all',
					    emptyText:'Select...',
					    valueNotFoundText: '???',
					    selectOnFocus:true
					});
				}
				else if(propDef.collectionType!="list")
				{
					config.width='300px';
					var f = new Ext.form.TextField(config);
				}
			}

			return f;
		},
	
		buildFormPart : function(td, prefix, hiddenFields, doNotAdd)
		{
			console.log(td.name);
			var fields = [];
			// Add fields according to typeDef
			for(var i=0; i<td.properties.length; i++)
			{
				var propDef = td.properties[i];
				var f = OO.EditForm.buildField(propDef, prefix, hiddenFields, doNotAdd);
				if(doNotAdd)
					fields.push(f);
				else if(f && f.isListField)
					form.fieldset({legend:f.label},f);
				else if(f)
					form.add(f);
			}
			if(doNotAdd)
				return fields;
			
			
		},

        buildNewForm : function(typeDef)
		{
			// FIXME Temp dupe of buildForm for testing
			Ext.QuickTips.init();

			// Show form validation warnings next to each field
			Ext.form.Field.prototype.msgTarget = 'side';

			// Create map to store hidden fields
			var hiddenFields = {};
            hiddenFields["_oo_containerId"] = OO.Workbench.currentContainer;
            hiddenFields["_oo_type"] = typeDef.name;


            // Create form
		    form = new Ext.form.Form({
				labelAlign: 'left',
				buttonAlign: 'left',
				monitorValid: true,
				labelWidth: 120
		    });

			// Add essential form processing support fields
			form.add(new Ext.form.TextField({fieldLabel:'ID', name:'id', width:'200px', allowBlank:true, disabled:true}));

			// Add form fields
			OO.EditForm.buildFormPart(typeDef, "", hiddenFields);

		    form.addButton('Save', function() {
				form.submit({url:OO.Workbench.getPath('/otherobjects/form'), bindForm:true, waitMsg:'Saving Data...', params:hiddenFields });
			});
		    form.addButton('Save and continue editing', function() {
				form.submit({url:OO.Workbench.getPath('/otherobjects/form'), bindForm:true, waitMsg:'Saving Data...', params:hiddenFields });
			});

			form.on('actioncomplete', function(event,action) {
				// Update listing panel
				console.log("Form saved correctly.");
				//OO.ListingGrid.updateItem(action.result.formObject);
				OO.Workbench.getPanel("preview-panel").setDirty(true);
				OO.Workbench.activatePanel("listing-panel");
			}, this);
			//form.on('actionfailed', showFailureMessage, this);

			// Remove previous form and render new one
			Ext.get('edit-panel').dom.innerHTML='';
		    form.render('edit-panel');

			// Set form values
			//var v = OO.EditForm.flattenObject(obj, "", []);
			//  form.setValues(v);
		},

        buildForm : function(obj)
		{
			// TODO Find out about QuickTips
			Ext.QuickTips.init();
			
			// Show form validation warnings next to each field
			Ext.form.Field.prototype.msgTarget = 'side';
	
			// Create map to store hidden fields
			var hiddenFields = {};
			hiddenFields["id"] = obj.id;
			hiddenFields["editableId"] = obj.editableId;
			
			// Create form
		    form = new Ext.form.Form({
				labelAlign: 'left',
				buttonAlign: 'left',
				monitorValid: true,
				labelWidth: 120
		    });
	
			// Add essential form processing support fields	
			form.add(new Ext.form.TextField({fieldLabel:'ID', name:'id', width:'200px', allowBlank:true, disabled:true}));
			
			// Add form fields
			OO.EditForm.buildFormPart(obj.typeDef, "", hiddenFields);
				
		    form.addButton('Save', function() {
				form.submit({url:OO.Workbench.getPath('/otherobjects/form'), bindForm:true, waitMsg:'Saving Data...', params:hiddenFields });
			});
		    form.addButton('Save and continue editing', function() {
				form.submit({url:OO.Workbench.getPath('/otherobjects/form'), bindForm:true, waitMsg:'Saving Data...', params:hiddenFields });   
			});
			
			form.on('actioncomplete', function(event,action) {
				// Update listing panel
				console.log("Form saved correctly.");
				OO.ListingGrid.updateItem(action.result.formObject);
				OO.Workbench.getPanel("preview-panel").setDirty(true);
				OO.Workbench.activatePanel("listing-panel");
			}, this);
			//form.on('actionfailed', showFailureMessage, this);
			
			// Remove previous form and render new one
			Ext.get('edit-panel').dom.innerHTML='';
		    form.render('edit-panel');
			
			// Set form values
			var v = OO.EditForm.flattenObject(obj, "", []);
			form.setValues(v);
		},
	
		flattenObject : function(obj,prefix,flat) {
			// FIXME Produces too many results.
			for(var id in obj) {
		    	if(typeof obj[id] != 'function' && id != "typeDef") {
					if(obj[id] instanceof Object) {
						if(id == "data") {
							// FIXME Quick hack since Json is encoding DynaNodes wrong
							console.log(prefix+id,obj[id]);
							flat[flat.length]={id:prefix, value:obj[id]};
							OO.EditForm.flattenObject(obj[id],"",flat);
						}
						else {
							console.log(prefix+id,obj[id]);
							flat[flat.length]={id:prefix+id, value:obj[id]};
							OO.EditForm.flattenObject(obj[id],id+".",flat);
						}
					}
					else {
						console.log(prefix+id +"="+ obj[id]);
						flat[flat.length]={id:prefix+id, value:obj[id]};
		        	}
				}
		    }
		    return flat;
		},

		loadJsonObject : function(url, callback) {
			Ext.Ajax.request({url:url, success:function(r) {
				var o = Ext.util.JSON.decode(r.responseText);
				callback(o);
			}});
		},


    	createForm : function(id) {
			this.dataId = id;
    	},

        createNewForm : function(typeDef) {
                     this.newDataType = typeDef;
            this.dataId = null;
        },


        renderForm : function()	{
            if(this.dataId)
                OO.EditForm.loadJsonObject(OO.Workbench.getPath("/otherobjects/data/item/") + this.dataId, OO.EditForm.buildForm);
            else
                OO.EditForm.buildNewForm(this.newDataType)

        }
	}

}();

// ------------------------------------------------------------------------------------------------------------------------------
// Custom DateField that supports Java style dates in milliseconds
// ------------------------------------------------------------------------------------------------------------------------------

Ext.form.OODateField = function(config) {
    Ext.form.OODateField.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.OODateField, Ext.form.DateField, {
  	setValue : function(date) {
		if(date)
			Ext.form.DateField.superclass.setValue.call(this, this.formatDate(new Date(date)));
    }
});

// ------------------------------------------------------------------------------------------------------------------------------
// Json backed combo field
// ------------------------------------------------------------------------------------------------------------------------------

Ext.form.OOComboBox = function(config){
    Ext.form.OOComboBox.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.OOComboBox, Ext.form.ComboBox,  {

    setValue : function(v){
		console.log("Combo value", v);
	
    	var text = v;
    	if(v && v.id)
    	{
    		text = v.label;
    		v = v.id;
    	}
        else if(v && this.valueField){
            var r = this.findRecord(this.valueField, v);
            if(r){
                text = r.data[this.displayField];
            }else if(this.valueNotFoundText){
                text = this.valueNotFoundText;
            }
        }
        this.lastSelectionText = text;
        if(this.hiddenField){
            this.hiddenField.value = v;
        }
        Ext.form.ComboBox.superclass.setValue.call(this, text);
        this.value = v;
    },
    
     getValue : function(){
        console.log("Combo getValue");
		if(this.valueField){
            return typeof this.value != 'undefined' ? this.value : '';
        }else{
            return Ext.form.ComboBox.superclass.getValue.call(this);
        }
    },
	
	newSetValue : function(v){
		var id, label;
    	if(v && v.id)
    	{
    		label = v.label;
    		id = v.id;
    	}
        else
		{
			label = "Nothing selected";
			id = null; 
		}
        this.lastSelectionText = label;
            this.hiddenField.value = id;
        this.value = label;
    }
	
});