MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Textfield", null, false);
MWF.xApplication.process.FormDesigner.Module.Number = MWF.FCNumber = new Class({
	Extends: MWF.FCTextfield,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Number/number.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Number/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Number/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "number";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
    clearTemplateStyles: function(styles){
        if (styles){
            if (styles.styles) this.removeStyles(styles.styles, "styles");
            if (styles.inputStyles) this.removeStyles(styles.inputStyles, "inputStyles");
            if (styles.properties) this.removeStyles(styles.properties, "properties");
        }
    },
    setTemplateStyles: function(styles){
        if (styles.styles) this.copyStyles(styles.styles, "styles");
        if (styles.inputStyles) this.copyStyles(styles.inputStyles, "inputStyles");
        if (styles.properties) this.copyStyles(styles.properties, "properties");
    },
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "textfield",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	
		var icon = new Element("div", {
			"styles": this.css.textfieldIcon
		}).inject(this.moveNode);
		var text = new Element("div", {
			"styles": this.css.moduleText,
			"text": this.json.id
		}).inject(this.moveNode);
	},
    setPropertiesOrStyles: function(name){
        if (name=="styles"){
            // if (this.parentContainer){
            //     if (this.parentContainer.moduleName == "datagrid$Data"){
            //         if (!this.json.styles.width) this.json.styles.width = "90%";
            //     }
            // }
            this.setCustomStyles();
        }
        if (name=="properties"){
            this.node.setProperties(this.json.properties);
        }
    },

	_loadNodeStyles: function(){
		var icon = this.node.getFirst("div");
		var text = this.node.getLast("div");
        if (!icon) icon = new Element("div").inject(this.node, "top");
        if (!text) text = new Element("div").inject(this.node, "bottom");
		icon.setStyles(this.css.textfieldIcon);
		text.setStyles(this.css.moduleText);
	},
	_getCopyNode: function(){
		if (!this.copyNode) this._createCopyNode();
		this.copyNode.setStyle("display", "inline-block");
		return this.copyNode;
	},
	
	_setEditStyle_custom: function(name){
		if (name=="id"){
			this.node.getLast().set("text", this.json.id);
		}
	}

	
});
