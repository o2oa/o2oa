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
	_filterHtml: function(html){
		var reg = /(?:@|\:)\S*(?:\=)\S*(?:\"|\'|\s)/g;
		var v = html.replace(reg, "");

		// var tmp = new Element("div", {"html": v});
		// var nodes = tmp.querySelectorAll("*[v-model]");
		// this.tmpVueData = {};
		// nodes.forEach(function(node){
		// 	var model = node.get("v-model");
		// 	if (!model) model = o2.uuid();
		// 	node.set("v-model", model);
		// 	this.tmpVueData[model] = "";
		// }.bind(this));
		// var v = tmp.get("html");
		// tmp.destroy();
		return v;
	},
	_checkVueHtml: function(){
		var nodes = this.node.querySelectorAll("*[v-model]");
		this.tmpVueData = {};
		nodes.forEach(function(node){
			var model = node.get("v-model");
			if (!model){
				model = o2.uuid();
				node.set("v-model", model);
			}
			this.tmpVueData[model] = "";
		}.bind(this));
	},
	_resetVueModuleDomNode: function(callback){
		if (!this.vm){
			if (!this.node.hasClass("o2_vue")) this.node.addClass("o2_vue");
			var html = this._filterHtml(this._createElementHtml());

			this.node.set("html", html);
			this._checkVueHtml();

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
		try{
			this.vm = new Vue(this.vueApp);

			var p = {
				"$options": {
					"errorCaptured": function(err, vm, info){
						alert("p: errorCaptured:"+info);
					}
				}
			}
			this.vm.$parent = p;
			// this.vm.config.errorHandler = function (err, vm, info) {
			// 	alert("errorHandler: "+info)
			// }
			this.vm.$mount(this.node);
		}catch(e){
			this.node.store("module", this);
			this._loadVueCss();
			if (callback) callback();
		}

	},
	_createVueData: function(){
		var data = {};
		return Object.assign(data, this.tmpVueData||{});
	},
	_createVueExtend: function(callback){
		var _self = this;
		return {
			data: this._createVueData(),
			mounted: function(){
				_self._afterMounted(this.$el, callback);
			},
			errorCaptured: function(err, vm, info){
				alert("errorCaptured:"+info);
				// return false;
			}
		};
	},
	// _afterMounted: function(el, callback){
	// 	this.node = el;
	// 	this.node.store("module", this);
	// 	if (callback) callback();
	// },
	_loadVueCss: function(){
		if (this.styleNode){
			this.node.removeClass(this.styleNode.get("id"));
		}
		if (this.json.vueCss && this.json.vueCss.code){
			this.styleNode = this.node.loadCssText(this.json.vueCss.code, {"notInject": true});
			this.styleNode.inject(this.node, "top");
		}
	},
	_afterMounted: function(el, callback){
		this.node = el;
		this.node.store("module", this);
		this._loadVueCss();
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
	},
	_preprocessingModuleData: function(){
		try{
			this.node.clearStyles();
			this.node.removeAttribute("class");
			//if (this.initialStyles) this.node.setStyles(this.initialStyles);
			this.json.recoveryStyles = Object.clone(this.json.styles);

			if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
				if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
					//需要运行时处理
				}else{
					this.node.setStyle(key, value);
					delete this.json.styles[key];
				}
			}.bind(this));
			this.json.preprocessing = "y";
		}catch(e){};
	},
});
