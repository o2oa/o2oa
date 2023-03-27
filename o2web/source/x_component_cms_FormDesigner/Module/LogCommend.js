MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Log", null, false);
MWF.xApplication.cms.FormDesigner.Module.LogCommend = MWF.CMSFCLogCommend = new Class({
	Extends: MWF.FCLog,
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/LogCommend/logCommend.html"
	},
	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_process_FormDesigner/Module/Log/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Log/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "logCommend";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "logCommend",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	_createIcon: function(){
		this.iconNode = new Element("div", {
			"styles": this.css.iconNode
		}).inject(this.node);
		new Element("div", {
			"styles": this.css.iconNodeIcon
		}).inject(this.iconNode);
		new Element("div", {
			"styles": this.css.iconNodeText,
			"text": "CommendLOG"
		}).inject(this.iconNode);
	},
});
