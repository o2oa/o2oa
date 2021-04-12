MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Container", null, false);
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Component", null, false);
MWF.xApplication.process.FormDesigner.Module.Datatemplate = MWF.FCDatatemplate = new Class({
	Extends: MWF.FC$Container,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Datatemplate/datatemplate.html"
	},

	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_process_FormDesigner/Module/Datatemplate/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Datatemplate/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "datatemplate";

		this.form = form;
	},
	clearTemplateStyles: function(styles){
		if (styles){
			if (styles.styles) this.removeStyles(styles.styles, "styles");
			if (styles.properties) this.removeStyles(styles.properties, "properties");
		}
	},
	setTemplateStyles: function(styles){
		if (styles.styles) this.copyStyles(styles.styles, "styles");
		if (styles.properties) this.copyStyles(styles.properties, "properties");
	},
	_createMoveNode: function(){

		this.moveNode = new Element("div", {
			"MWFType": "datatemplate",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},

	// _dragComplete: function(){
	// 	if (!this.node){
	// 		this._createNode(function(){
	// 			this._dragMoveComplete();
	// 		}.bind(this));
	// 	}else{
	// 		this._dragMoveComplete();
	// 	}
	// },
	// _dragMoveComplete: function(){
	// 	this._resetTreeNode();
	// 	this.node.inject(this.copyNode, "before");
	//
	// 	this._initModule();
	//
	// 	var thisDisplay = this.node.retrieve("thisDisplay");
	// 	if (thisDisplay){
	// 		this.node.setStyle("display", thisDisplay);
	// 	}
	//
	// 	if (this.copyNode) this.copyNode.destroy();
	// 	if (this.moveNode) this.moveNode.destroy();
	// 	this.moveNode = null;
	// 	this.copyNode = null;
	// 	this.nextModule = null;
	// 	this.form.moveModule = null;
	//
	// 	this.form.json.moduleList[this.json.id] = this.json;
	// 	this.selected();
	// },

	_initModule: function(){
		if (!this.initialized){
			if (this.json.initialized!=="yes")this.setStyleTemplate();

			// this._getElements();
			// this._getContainers();

			this.setPropertiesOrStyles("styles");
			this.setPropertiesOrStyles("properties");

			this._setNodeProperty();
			if (!this.form.isSubform) this._createIconAction();

			//     this.checkSequenceShow();

			this._setNodeEvent();

			this.initialized = true;
			this.json.initialized = "yes";
		}
	},
	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			var border = this.node.getStyle("border");
			this.node.clearStyles();
			this.node.setStyles(this.css.moduleNode);
			this.node.setStyle("border", border);
			Object.each(this.json.styles, function(value, key){
				var reg = /^border\w*/ig;
				if (!key.test(reg)){
					this.node.setStyle(key, value);
				}
			}.bind(this));
		}

		if (name=="properties"){
			this.node.setProperties(this.json.properties);
		}
	},
	_setEditStyle_custom: function(name, obj, oldValue){
		if (name=="id"){
			if (name!=oldValue){
				var reg = new RegExp("^"+oldValue, "i");
				this.containers.each(function(container){
					var id = container.json.id;
					var newId = id.replace(reg, this.json.id);
					container.json.id = newId;

					delete this.form.json.moduleList[id];
					this.form.json.moduleList[newId] = container.json;
					container._setEditStyle("id");
				}.bind(this));
			}
		}
		//if (name=="sequence") this.checkSequenceShow();
	},
	setAllStyles: function(){
		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("properties");

		this.reloadMaplist();
	},

	getContainerNodes: function(){
		return this.node.getElements("td");
	},
	copyComponentJsonData: function(newNode, pid){
		var tds = newNode.getElements("td");
		var ths = newNode.getElements("th");
		tds.each(function(td, idx){
			var newContainerJson = Object.clone(this.containers[idx].json);
			newContainerJson.id = this.containers[idx]._getNewId(pid);
			this.form.json.moduleList[newContainerJson.id] = newContainerJson;
			td.set("id", newContainerJson.id);
		}.bind(this));
		ths.each(function(th, idx){
			var newElementJson = Object.clone(this.elements[idx].json);
			newElementJson.id = this.elements[idx]._getNewId(pid);
			this.form.json.moduleList[newElementJson.id] = newElementJson;
			th.set("id", newElementJson.id);
		}.bind(this));
	}

});
