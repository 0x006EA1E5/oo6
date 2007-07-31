// ------------------------------------------------------------------------------------------------------------------------------
// List field
// ------------------------------------------------------------------------------------------------------------------------------

Ext.form.OOListField = function(form, config){
	var i = 0;
	this.form = form;
    Ext.form.OOListField.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.OOListField, Ext.form.Field, {
	form : undefined,
	fields : undefined,
	layouts : undefined,
	
	setValue : function(data) {
		console.log("Set list field value", data);
		if(data) {
			for(var i=0; i < data.length; i++) {
				if(this.field == undefined || this.fields[i]==undefined)
					this.addField();
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
		console.log(this.form);
		var index = 0;
		if(this.fields) index = this.fields.length;
		var f = new Ext.form.TextField({fieldLabel:(index+1), name:this.name+'['+index+']', width:'200px', allowBlank:true, disabled:false});
		this.append(f);
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

	append : function(field) {
		
		if(!this.fields) this.fields = [];
		if(!this.layouts) this.layouts = [];
			
		// Create a new layout object
		var layout = new Ext.form.Layout({labelWidth: 120});
		layout.stack.push(field);

		// Render the layout
		console.log(this.fields);
		var neighbour;
		if(this.fields.length==0)
			neighbour = this.el;
		else
			neighbour = this.fields[this.fields.length-1].el;
		console.log(neighbour);
		neighbour = neighbour.findParent(".x-form-item");
		var c = Ext.DomHelper.insertAfter(neighbour, {tag:"div"});
		console.log(c);
		layout.render(c);
		
		this.form.items.add(field);
		
		field.render('x-form-el-' + field.id);
		this.layouts.push(layout);
		this.fields.push(field);
			
	} 
});