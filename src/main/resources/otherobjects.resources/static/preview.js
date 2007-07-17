/**
 * @class OO.PreviewPanel
 * @extends Ext.ContentPanel
 * @constructor
 * Create a new PreviewPanel containing an iframe. Iframe source url is set
 * in via config.src or setSrc().
 * @param {String} id The id for the panel
 * @param {Object} config A config object
 */
OO.PreviewPanel = function(id, config){
	this.src = config.src;
	this.currentSrc = config.src;
    this.wrapper = Ext.DomHelper.append(document.body, {tag: "iframe", id:id, src:config.src, frameborder:'no'}, true);
    OO.PreviewPanel.superclass.constructor.call(this, this.wrapper, config);
	this.on("activate", activate);
	
	function activate()
	{
		console.log("Current preview:" + this.currentSrc);
		if(this.currentSrc != this.src)
		{
			console.log("Loading new preview:" + this.src);
			this.wrapper.dom.src=this.src;
			this.currentSrc=this.src;			
		}
	}
};

Ext.extend(OO.PreviewPanel, Ext.ContentPanel, {
    
    setSize : function(width, height){
        if(!this.ignoreResize(width, height)){
            var w = this.wrapper;
            var size = this.adjustForComponents(width, height);
			w.dom.width=width;
			w.dom.height=height;
        }
    },
	
	setSrc : function(src) {
		this.src=src;
	}
	
//    beforeSlide : function(){
//        this.grid.getView().scroller.clip();
//    },
//    
//    afterSlide : function(){
//        this.grid.getView().scroller.unclip();
//    },
//    
//    destroy : function(){
//        this.grid.destroy();
//        delete this.grid;
//        Ext.GridPanel.superclass.destroy.call(this); 
//    }
});
