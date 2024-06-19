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
	_dragMoveComplete: MWF.FC$Module.prototype._dragComplete,
	_dragComplete: function(){
		if (!this.node){
			this._createNode(function(){
				this._dragMoveComplete();
			}.bind(this));
		}else{
			this._dragMoveComplete();
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
		if (!window.Vue || window.Vue.name!=='Vue'){
			o2.load(["vue", "elementui"], { "sequence": true }, function(){
				if( window.Vue.config )window.Vue.config = {};
				window.Vue.config.errorHandler = function (err, vm, info) {
					if (vm.$o2module && info=="nextTick"){
						vm.$o2module._createVueAppNode();
						vm.$o2module.node.setStyles(vm.$o2module.css.moduleNodeError);
						vm.$el.parentNode.replaceChild(vm.$o2module.node, vm.$el);
						vm.$el = vm.$o2module.node;
						if (vm.$o2callback) vm.$o2module._afterMounted(vm.$o2module.node, vm.$o2callback);
					}
				}
				if (callback) callback();
			});
		}else{
			if (callback) callback();
		}
	},
	_createElementHtml: function(){
		return "";
	},
	_filterHtml: function(html){
		var reg = /(?:@|v-on|v-model|:data)\S*(?:\=)\S*(?:\"|\'|\s)/g;
		var v = html.replace(reg, "");
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
		//var data = this.json;
		return function(){
			this.setElStyles();
			return Object.assign(this.json, this.tmpVueData||{});
		}.bind(this)
	},
	_createVueExtend: function(callback){
		var _self = this;
		return {
			data: this._createVueData(),
			mounted: function(){
				this.$nextTick(function(){
					_self._afterMounted(this.$el, callback);
				});
			},
			errorCaptured: function(err, vm, info){
				//alert("errorCaptured:"+info);
				return false;
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
	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			try{
				this.setCustomStyles();
			}catch(e){}
		}else if (name=="elStyles"){
			this.setElStyles();
		}else if (name=="properties"){
			try{
				this.setCustomProperties();
			}catch(e){}
		}
	},
	setElStyles: function(){
		var tmpElStyles = Object.clone(this.json.elStyles || {});
		if( tmpElStyles.display )delete tmpElStyles.display;
		if (this.json.elStyles) Object.each(this.json.elStyles, function(value, key){
			if (key){
				if (key.toString().toLowerCase()==="display"){
					if (value.toString().toLowerCase()==="none"){
						tmpElStyles["opacity"] = 0.3;
					}else{
						tmpElStyles["opacity"] = 1;
					}
				}else{
					tmpElStyles[key] = value;
				}
			}
		}.bind(this));
		this.json.tmpElStyles = tmpElStyles;
	},
	_setEditStyle_custom: function(name){
		switch (name){
			case "name": this.setPropertyName(); break;
			case "id": this.setPropertyId(); break;
			case "buttonRadio":
			case "vueSlot":
				if (this.isPropertyLoaded) if (this.vm) this.resetElement(); break;
			default: ;
		}
	},
	setPropertyName: function(){},
	setPropertyId: function(){},
	resetElement: function(){
		this.reseting = true;

		var node = this.vm.$el;
		this.vm.$destroy();
		node.empty();

		this.vm = null;

		this.isSetEvents = false;
		this._resetVueModuleDomNode(function(){
			this._setNodeProperty();
			if (!this.form.isSubform) this._createIconAction();
			this._setNodeEvent();
			this.reseting = false;
		}.bind(this));
		if (this._resetElementFun){
			this.form.removeEvent("postSave", this._resetElementFun);
			this._resetElementFun = null;
		}

	},
	_preprocessingModuleData: function(){
		if (this.node.nodeType===Node.COMMENT_NODE){
			var tmp = this.node;
			this._createVueAppNode();
			this.node.inject(tmp, "after");
			this.node.store("module", this);
			tmp.destroy();
		}
		try{
			this.node.empty();
			this.node.clearStyles();
			this.node.removeAttribute("class");
			//if (this.initialStyles) this.node.setStyles(this.initialStyles);

			if( this.json.tmpElStyles )delete this.json.tmpElStyles;

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

			this._resetElementFun = this.resetElement.bind(this);
			this.form.addEvent("postSave", this._resetElementFun);
		}catch(e){};
	},
});
