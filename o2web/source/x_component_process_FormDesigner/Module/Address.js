MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Address = MWF.FCAddress = new Class({
	Extends: MWF.FCCombox,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Address/address.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Address/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Address/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "address";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
    _loadNodeStyles: function(){
        var icon = this.node.getFirst("div");
        var text = this.node.getLast("div");
        if (!icon) icon = new Element("div").inject(this.node, "top");
        if (!text) text = new Element("div").inject(this.node, "bottom");
        icon.setStyles(this.css.addressIcon);
        text.setStyles(this.css.moduleText);
    },
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "address",
			"styles": this.css.moduleNodeMove,
			"id": this.json.id,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
		var icon = new Element("div", {
			"styles": this.css.addressIcon
		}).inject(this.moveNode);
		var text = new Element("div", {
			"styles": this.css.moduleText,
			"text": this.json.id
		}).inject(this.moveNode);
	}
	
});
