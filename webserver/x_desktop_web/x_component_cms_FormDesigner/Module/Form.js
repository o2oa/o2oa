MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Form", null, false);
MWF.xDesktop.requireApp("cms.FormDesigner", "Property", null, false);
MWF.xApplication.cms.FormDesigner.Module.Form = MWF.CMSFCForm = new Class({
	Extends: MWF.FCForm,
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Form/form.html",
		"mode": "PC"
	},
	initialize: function(designer, container, options){
		this.setOptions(options);

		this.path = "/x_component_process_FormDesigner/Module/Form/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Form/"+this.options.style+"/css.wcss";

		this._loadCss();

		this.container = null;
		this.form = this;
		this.moduleType = "form";

		this.moduleList = [];
		this.moduleNodeList = [];

		this.moduleContainerNodeList = [];
		this.moduleElementNodeList = [];
		this.moduleComponentNodeList = [];

		//	this.moduleContainerList = [];
		this.dataTemplate = {};

		this.designer = designer;
		this.container = container;

		this.selectedModules = [];
	},

    loadStylesList: function(callback){
		var stylesUrl = "/x_component_cms_FormDesigner/Module/Form/template/"+((this.options.mode=="Mobile") ? "styles": "styles")+".json";
		MWF.getJSON(stylesUrl,{
				"onSuccess": function(responseJSON){
					this.stylesList= responseJSON;
					if (callback) callback(this.stylesList);
				}.bind(this),
				"onRequestFailure": function(){
					this.stylesList = {};
					if (callback) callback(this.stylesList);
				}.bind(this),
				"onError": function(){
					this.stylesList = {};
					if (callback) callback(this.stylesList);
				}.bind(this)
			}
		);
    },
	
	loadModule: function(json, dom, parent){
		//var classPre = "CMSFC";
		//var module = new MWF[classPre+json.type](this);
		//module.load(json, dom, parent);
		//return module;

		if( MWF["CMSFC"+json.type] ){
			var module = new MWF["CMSFC"+json.type](this);
			module.load(json, dom, parent);
			return module;
		}else{
			var module = new MWF["CMSFCDiv"](this);
			module.load(json, dom, parent);
			return module;
		}
	},

	
	createModule: function(className, e){
		//var classPre = MWF.CMSFD.RedesignModules.indexOf( className.toLowerCase() ) != -1 ? "CMSFC" : "FC";
		var classPre = "CMSFC";
		this.getTemplateData(className, function(data){
			var moduleData = Object.clone(data);
			var newTool = new MWF[classPre+className](this);
			newTool.create(moduleData, e);
		}.bind(this));
	},
	getTemplateData: function(className, callback){
		if (this.dataTemplate[className]){
			if (callback) callback(this.dataTemplate[className]);
		}else{
			var path = MWF.CMSFD.ResetTemplateModules.indexOf( className.toLowerCase() ) != -1 ? "x_component_cms_FormDesigner" : "x_component_process_FormDesigner";
			var templateUrl = "/"+path+"/Module/"+className+"/template.json";
			MWF.getJSON(templateUrl, function(responseJSON, responseText){
				this.dataTemplate[className] = responseJSON;
				if (callback) callback(responseJSON);
			}.bind(this));
		}
	},
	showProperty: function(){
		if (!this.property){
			this.property = new MWF.xApplication.cms.FormDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
				"path": this.options.propertyPath,
				"onPostLoad": function(){
					this.property.show();
				}.bind(this)
			});
			this.property.load();
		}else{
			this.property.show();
		}
	},
	save: function(callback){
        this.designer.saveForm();
		//this._getFormData();
		//this.designer.actions.saveForm(this.data, function(responseJSON){
		//	this.form.designer.notice(MWF.APPFD.LP.notice["save_success"], "ok", null, {x: "left", y:"bottom"});
        //
		//	//this.json.id = responseJSON.data;
		//	if (!this.json.name) this.treeNode.setText("<"+this.json.type+"> "+this.json.id);
		//	this.treeNode.setTitle(this.json.id);
		//	this.node.set("id", this.json.id);
		//
		//	if (callback) callback();
		//	//this.reload(responseJSON.data);
		//}.bind(this));
	},
	preview: function(){

		MWF.xDesktop.requireApp("cms.FormDesigner", "Preview", function(){

			if (this.options.mode=="Mobile"){
				this.previewBox = new MWF.xApplication.cms.FormDesigner.Preview(this, {"size": {"x": "340", "y": 580}});
			}else{
				this.previewBox = new MWF.xApplication.cms.FormDesigner.Preview(this);
			}
			this.previewBox.load();

		}.bind(this));
	}
});
