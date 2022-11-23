MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Relatedlink = MWF.FCRelatedlink = new Class({
	Extends: MWF.xApplication.process.FormDesigner.Module.$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Relatedlink/relatedlink.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Relatedlink/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Relatedlink/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "relatedlink";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "relatedlink",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	_createNode: function(){
		this.node = this.moveNode.clone(true, true);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("id", this.json.id);
		this.node.addEvent("selectstart", function(){
			return false;
		});

		this.loadIcon();
	},
	loadIcon: function(){
		this.iconNode = new Element("div", {
			"styles": this.css.iconNode
		}).inject(this.node);
		new Element("div", {
			"styles": this.css.iconNodeIcon
		}).inject(this.iconNode);
		new Element("div", {
			"styles": this.css.iconNodeText,
			"text": "Relatedlink"
		}).inject(this.iconNode);
	}

});
