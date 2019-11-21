MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("cms.FormDesigner", "Module.Org", null, false);
MWF.xApplication.cms.FormDesigner.Module.Reader = MWF.CMSFCReader = new Class({
	Extends: MWF.CMSFCOrg,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Org/org.html"
	},
	initialize: function(form, options){
		this.setOptions(options);

		this.path = "/x_component_cms_FormDesigner/Module/Reader/";
		this.cssPath = "/x_component_cms_FormDesigner/Module/Reader/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "reader";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "reader",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);

		var icon = new Element("div", {
			"styles": this.css.fieldIcon
		}).inject(this.moveNode);
		var text = new Element("div", {
			"styles": this.css.moduleText,
			"text": this.json.id
		}).inject(this.moveNode);
	}
});
