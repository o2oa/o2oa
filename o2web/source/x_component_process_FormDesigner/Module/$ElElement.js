MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.$ElElement = MWF.FC$ElElement = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],

	_initModuleType: function(){
		this.className = "Elbutton";
		this.moduleType = "element";
		this.moduleName = "elbutton";
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
	_dragMoveComplete: MWF.FC$Component.prototype._dragComplete,
	_dragComplete: function(){
		if (!this.node){
			this._createNode(function(){
				this._dragMoveComplete();
			}.bind(this));
		}else{
			this._dragMoveComplete();
		}
	},
	showProperty: function(){
		if (!this.property){
			this.property = new MWF.xApplication.process.FormDesigner.Property(this, this.form.designer.propertyContentArea, this.form.designer, {
				"path": this.options.propertyPath,
				"onPostLoad": function(){
					this.property.show();
					this.isPropertyLoaded = true;
				}.bind(this)
			});
			this.property.load();
		}else{
			this.property.show();
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
	_createVueAppNode: function(){
		this.node = new Element("div.o2_vue", {
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
	_createNode: function(callback){
		this._createVueAppNode();
		this._resetVueModuleDomNode(callback);
	},
	_initModule: function(){
		if (!this.json.isSaved) this.setStyleTemplate();
		this._resetVueModuleDomNode(function(){
			this._setNodeProperty();
			if (!this.form.isSubform) this._createIconAction();
			this._setNodeEvent();
			//this.selected(true);
		}.bind(this));
		this.json.isSaved = true;
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
	_resetVueModuleDomNode: function(callback){
		if (!this.vm){
			this.node.set("html", this._createElementHtml());
			this._loadVue(function(){
				this._mountVueApp(callback);
			}.bind(this));
		}else{
			if (callback) callback();
		}
	},
	_resetModuleDomNode: function(){

	},
	_mountVueApp: function(callback){
		//if (!this.vueApp)
		this.vueApp = this._createVueExtend(callback);
		this.vm = new Vue(this.vueApp);
		this.vm.$mount(this.node);
	},
	_createVueData: function(){
		var data = {};
		return data;
	},
	_createVueExtend: function(callback){
		var _self = this;
		return {
			data: this._createVueData(),
			mounted: function(){
				_self._afterMounted(this.$el, callback);
			}
		};
	},
	_afterMounted: function(el, callback){
		this.node = el;
		this.node.store("module", this);
		if (callback) callback();
	},
	_setOtherNodeEvent: function(){},

	_setEditStyle_custom: function(name){
		debugger;
		switch (name){
			case "name": this.setPropertyName(); break;
			case "id": this.setPropertyId(); break;
			default: if (this.isPropertyLoaded) if (this.vm) this.resetElement();
		}
	},
	setPropertyName: function(){},
	setPropertyId: function(){},
	resetElement: function(){
		//this._createVueAppNode();
		//this.node.inject(this.vm.$el,"before");
		// var node = this.vm.$el;
		// this.vm.$destroy();
		// node.destroy();

		var node = this.vm.$el;
		this.vm.$destroy();
		node.empty();

		this.vm = null;

		this.isSetEvents = false;
		this._resetVueModuleDomNode(function(){
			this._setNodeProperty();
			if (!this.form.isSubform) this._createIconAction();
			this._setNodeEvent();
		}.bind(this));
	}
});
