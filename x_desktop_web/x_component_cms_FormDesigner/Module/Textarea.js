MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("cms.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.cms.FormDesigner.Module.Textarea = MWF.CMSFCTextarea = new Class({
	Extends: MWF.CMSFC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Textarea/textarea.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_cms_FormDesigner/Module/Textarea/";
		this.cssPath = "/x_component_cms_FormDesigner/Module/Textarea/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "textarea";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "textarea",
			"styles": this.css.moduleNodeMove,
			"id": this.json.id,
			"readonly": true,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
		var icon = new Element("div", {
			"styles": this.css.textareaIcon
		}).inject(this.moveNode);
		var text = new Element("div", {
			"styles": this.css.moduleText,
			"text": this.json.id
		}).inject(this.moveNode);
	},
	_loadNodeStyles: function(){
		var icon = this.node.getFirst("div");
		var text = this.node.getLast("div");
		icon.setStyles(this.css.textareaIcon);
		text.setStyles(this.css.moduleText);
	},
	_getCopyNode: function(){
		if (!this.copyNode) this._createCopyNode();
		this.copyNode.setStyle("display", "inline-block");
		return this.copyNode;
	},
	
	_setEditStyle_custom: function(name){
		if (name=="id"){
			this.node.getLast().set("text", this.json.id);
		}
	}
	
});
