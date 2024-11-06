MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.$ElComponent = MWF.FC$ElComponent = new Class({
	Extends: MWF.FC$Component,
	Implements: [Options, Events],

	_initModuleType: function(){
		this.className = "ElComponent"
		this.moduleType = "component";
		this.moduleName = "elComponent";
	},

	initialize: function(form, options){
		this.setOptions(options);
		this._initModuleType();
		this.path = "../x_component_process_FormDesigner/Module/"+this.className+"/";
		this.cssPath = "../x_component_process_FormDesigner/Module/"+this.className+"/"+this.options.style+"/css.wcss";
		this._loadCss();
		this.form = form;
		this.container = null;
		this.containerNode = null;
		this.isPropertyLoaded = false;
	},
	showProperty: function(callback){
		if (!this.property){
			this.property = new MWF.xApplication.process.FormDesigner.Property(this, this.form.designer.propertyContentArea, this.form.designer, {
				"path": this.options.propertyPath,
				"onPostLoad": function(){
					this.property.show();
					this.isPropertyLoaded = true;
					if (callback) callback();
				}.bind(this)
			});
			this.property.load();
		}else{
			this.property.show();
			if (callback) callback();
		}
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": this.moduleName,
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"text": this.json.name || this.json.id,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	_createNode: function(){
		this.node = new Element("div", {
			"MWFType": this.moduleName,
			"id": this.json.id,
			"styles": this.css.moduleNode,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		});
	},
	_initModule: function(){
		if (!this.initialized){
			if (this.json.initialized!=="yes") this.setStyleTemplate();
			this._resetModuleDomNode();
		}
	},
	_loadVue: function(callback){
		if (!window.Vue){
			o2.load(["vue", "elementui"], { "sequence": true }, callback);
		}else{
			if (callback) callback();
		}
	},
	_createElementHtml: function(){
		return "";
	},
	_resetModuleDomNode: function(){
		if (!this.vm){
			this.node.set("html", this._createElementHtml());
			this._loadVue(this._mountVueApp.bind(this));
		}
	},
	_mountVueApp: function(){
		if (!this.vueApp) this.vueApp = this._createVueExtend();
		try{
			this.vm = new Vue(this.vueApp);
			this.vm.$o2module = this;
			this.vm.$o2callback = callback;

			this.vm.$mount(this.node);
		}catch(e){
			this.node.store("module", this);
			this._loadVueCss();
			if (callback) callback();
		}
	},
	_createVueData: function(){
		return {};
	},
	_createVueExtend: function(){
		var _self = this;
		return {
			data: this._createVueData(),
			mounted: function(){
				_self._afterMounted(this.$el);
			}
		};
	},
	_afterMounted: function(el){
		this.node = el;
		this.node.store("module", this);

		this._getContainers();
		this._getElements();

		this._setNodeProperty();

		if (!this.form.isSubform) this._createIconAction();
		this._setNodeEvent();

		this.initialized = true;
		this.json.initialized = "yes";

		this.selected(true);
	},

	_setOtherNodeEvent: function(){},

	_setEditStyle_custom: function(name){
		switch (name){
			case "name": this.setPropertyName(); break;
			case "id": this.setPropertyId(); break;
			default: if (this.isPropertyLoaded) if (this.vm) this.resetElement();
		}
	},
	setPropertyName: function(){},
	setPropertyId: function(){},
	resetElement: function(){
		this._createNode();
		this.node.inject(this.vm.$el,"before");
		var node = this.vm.$el;
		this.vm.$destroy();
		node.destroy();
		this.vm = null;
		this.isSetEvents = false;
		this._resetModuleDomNode();
	}
});
