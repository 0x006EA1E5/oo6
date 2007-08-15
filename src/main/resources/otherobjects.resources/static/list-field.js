// ------------------------------------------------------------------------------------------------------------------------------
// List field
//
// Some ideas from: http://extjs.com/forum/showthread.php?t=5743
// ------------------------------------------------------------------------------------------------------------------------------

Ext.form.OOListField = function(form, propDef, config){
	var i = 0;
	this.form = form;
	this.propertyDef =  propDef;
    Ext.form.OOListField.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.OOListField, Ext.form.Field, {
	fields : undefined,
	layouts : undefined,
	isListField : true,
	
	setValue : function(data) {
		console.log("Set list field value", data);
		if(data) {
			for(var i=0; i < data.length; i++) {
				if(this.field == undefined || this.fields[i]==undefined)
					this.addField();
				console.log("XXX", this.fields[i],data[i]);
				if(this.fields[i].length) {
					// Component
					for(var j=0; j < this.fields[i].length; j++) {
						var f = this.fields[i][j];
						var name = f.name.substring(f.name.indexOf(".")+1);
						console.log(name, data[i], data[i][name]);
						f.setValue(data[i][name]);
					}
				}
					
				else
					this.fields[i].setValue(data[i]);
			}
		}
    },
	
	onRender : function(ct, position) {
		// Create buttons
 		this.el = ct.createChild({html:'<table cellspacing="0"><tbody><tr></tr></tbody></table><div class="x-clear"></div>'}, null, true);
        var btnContainer = this.el.firstChild.firstChild.firstChild;
		this.el = ct.createChild({tag: "div"}, position);
		var add = new Ext.Button(btnContainer.appendChild(document.createElement("td")), {text:"Add",enabled:true});
		add.on('click', function(e) { this.addField(); }, this);
		var remove = new Ext.Button(btnContainer.appendChild(document.createElement("td")), {text:"Remove",enabled:true});
		remove.on('click', function(e) { this.removeField(); }, this);
    },

	addField : function() {
		var index = 0;
		if(this.fields) index = this.fields.length;
		var pd = {name:this.name+'['+index+']', type:this.propertyDef.collectionElementType, relatedType:this.propertyDef.relatedType, relatedTypeDef:this.propertyDef.relatedTypeDef, label:(index+1)};
		var f = OO.EditForm.buildField(pd, "", [], true);
		this.append(f, this.propertyDef.label, index);
	},
	
	removeField : function() {
		var field = this.fields.pop();
		var layout = this.layouts.pop();
  		this.form.remove(field);
  		layout.destroy();
  		field.destroy();
  		delete layout;
  		delete field;
	},

	append : function(field, label, index) {
		if(!this.fields) this.fields = [];
		if(!this.layouts) this.layouts = [];
		
		
		if(field.length)
		{
			// Create a new layout object
			var layout = new Ext.form.FieldSet({labelWidth: 120, legend: "Item " + index});
	
			for(var i=0; i < field.length; i++)			
			{
				if(field[i].isFormField) {
					// FIXME Add FieldSet to list fields here
					//layout.stack.push(new Ext.form.FieldSet({labelWidth: 120, legend: "ItemX"}));
					layout.stack.push(field[i]);
					this.form.items.add(field[i]);
				}
			}
			// Render the layout
			var neighbour;
			if(this.fields.length==0){
				neighbour = this.el;
				neighbour = neighbour.findParent(".x-form-item");
			}
			else {
				var lastField = this.fields[this.fields.length-1];
				if(lastField.length)
					neighbour = lastField[lastField.length-1].el.findParent("fieldset");
				else
					neighbour = lastField.el;
			}
			var c = Ext.DomHelper.insertAfter(neighbour, {tag:"div"});
			layout.render(c);

			for(var i=0; i < field.length; i++)			
			{
				if(field[i].isFormField) {
					field[i].render('x-form-el-' + field[i].id);
				}
			}

			this.fields.push(field);
			this.layouts.push(layout);
			
		}
		else
		{
			// Create a new layout object
			var layout = new Ext.form.Layout({labelWidth: 120});
			layout.stack.push(field);
	
			// Render the layout
			var neighbour;
			if(this.fields.length==0)
				neighbour = this.el;
			else
				neighbour = this.fields[this.fields.length-1].el;
			neighbour = neighbour.findParent(".x-form-item");
			var c = Ext.DomHelper.insertAfter(neighbour, {tag:"div"});
			layout.render(c);
			
			this.form.items.add(field);
			field.render('x-form-el-' + field.id);
			
			this.layouts.push(layout);
			this.fields.push(field);
		}	
	}
	
});