MWF.xApplication.portal.PageDesigner.Module.Application = MWF.PCApplication = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Application/application.html",
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Application/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Application/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "application";
		
		this.Node = null;
		this.form = form;
		this.page = form;
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "application",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	_initModule: function(){
		if (!this.json.isSaved) this.setStyleTemplate();

		this._resetModuleDomNode();

		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("properties");

		this._setNodeProperty();
		if (!this.form.isSubform) this._createIconAction();
		this._setNodeEvent();
		this.json.isSaved = true;
	},
	_setEditStyle_custom: function(name){
		if (name=="size"){
			if (this.json[name]=="min"){
				this.attachmentController.changeControllerSizeToMin();
			}else{
				this.attachmentController.changeControllerSizeToMax();
			}
		}
	},
});
