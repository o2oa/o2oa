MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Log = MWF.FCLog = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Log/log.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Log/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Log/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "log";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	load : function(json, node, parent){

		this.json = json;
		this.node= node;
		this.node.store("module", this);
		
		//this.node.empty();
		
		this.node.setStyles(this.css.moduleNode);
		this.node.set("data-mwf-el-type", "MWFFormDesignerLog");

		this._loadNodeStyles();
		
		this._initModule();
		this._loadTreeNode(parent);

        this.setCustomStyles();
		
		this.parentContainer = this.treeNode.parentNode.module;
        this._setEditStyle_custom("id");
        
		this.parseModules();
        this.json.moduleName = this.moduleName;
	},
	clearTemplateStyles: function(styles){
		if (this.json.templateType) {
			if (styles) {
				if (styles[this.json.templateType]){
					if (styles[this.json.templateType].styles) this.removeStyles(styles[this.json.templateType].styles, "styles");
					if (styles[this.json.templateType].properties) this.removeStyles(styles[this.json.templateType].properties, "properties");
				}
			}
		}
	},

	setTemplateStyles: function(styles){
		if (this.json.templateType){
			if (styles[this.json.templateType]){
				var t = styles[this.json.templateType];
				if (t.styles) this.copyStyles(t.styles, "styles");
				if (t.properties) this.copyStyles(t.properties, "properties");
				if (t.textStyle) this.json.textStyle = t.textStyle;
				if (t.textTaskStyle) this.json.textTaskStyle = t.textTaskStyle;
				if (t.mode) this.json.mode = t.mode;
			}
		}
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "log",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	_createNode: function(){
		this.node = this.moveNode.clone(true, true);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("id", this.json.id);
		this.node.set("data-mwf-el-type", "MWFFormDesignerLog");
		this.node.addEvent("selectstart", function(){
			return false;
		});
		this._createIcon();
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
			"text": "LOG"
		}).inject(this.iconNode);
	},
	_loadNodeStyles: function(){
		this.iconNode = this.node.getElement("div").setStyles(this.css.iconNode);
		this.iconNode.getFirst("div").setStyles(this.css.iconNodeIcon);
		this.iconNode.getLast("div").setStyles(this.css.iconNodeText);
	},
	// _preprocessingModuleData: function(){
	// 	this.node.clearStyles();
	// 	this.json.recoveryStyles = Object.clone(this.json.styles);
	// 	if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
	// 		if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
	// 			//需要运行时处理
	// 		}else{
	// 			this.node.setStyle(key, value);
	// 			delete this.json.styles[key];
	// 		}
	// 	}.bind(this));
	// 	this.node.empty();
	// 	this.json.preprocessing = "y";
	// },
	// _recoveryModuleData: function(){
	// 	if (this.json.recoveryStyles) this.json.styles = this.json.recoveryStyles;
	// 	this.json.recoveryStyles = null;
	// 	this._createIcon();
	// }
	_setEditStyle_custom: function(name, obj, oldValue){
		if (name=="templateType"){
			if (this.form.templateStyles){
				var moduleStyles = this.form.templateStyles[this.moduleName];
				if (moduleStyles) {
					if (oldValue){
						if (moduleStyles[oldValue]){
							this.removeStyles(moduleStyles[oldValue].styles, "styles");
							this.removeStyles(moduleStyles[oldValue].styles, "properties");
						}
					}

					if (moduleStyles[this.json.templateType]){
						var t = moduleStyles[this.json.templateType];
						if (t.styles) this.copyStyles(t.styles, "styles");
						if (t.styles) this.copyStyles(t.properties, "properties");
						if (t.textStyle) this.json.textStyle = t.textStyle;
						if (t.textTaskStyle) this.json.textTaskStyle = t.textTaskStyle;
						if (t.mode) this.json.mode = t.mode;

						this._setHtmlAreaValue("textStyle");
						this._setHtmlAreaValue("textTaskStyle");

						var node = this.property.propertyContent.querySelector("input[name$='mode'][value='"+this.json.mode+"']");
						if (node){
							node.checked = true;
						}

					}

					this.setPropertiesOrStyles("styles");
					this.setPropertiesOrStyles("properties");

					this.reloadMaplist();
				}
			}
		}
	},
	_setHtmlAreaValue: function(name){
		var node = this.property.propertyContent.querySelector(".MWFHtmlEditorArea[name='"+name+"']");
		if (node.htmlArea.jsEditor){
			node.htmlArea.jsEditor.setValue(this.json.textStyle);
		}else{
			node.htmlArea.htmlContentData.code = this.json.textStyle;
		}
	}
});
