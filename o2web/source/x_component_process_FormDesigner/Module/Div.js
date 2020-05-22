MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Container", null, false);
MWF.xApplication.process.FormDesigner.Module.Div = MWF.FCDiv = new Class({
	Extends: MWF.FC$Container,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Div/div.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Div/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Div/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "div";
		
		this.Node = null;
		this.form = form;
	},
    clearTemplateStyles: function(styles){
        if (this.json.templateType){
            if (styles){
                if (styles[this.json.templateType]){
                    if (styles[this.json.templateType].styles) this.removeStyles(styles[this.json.templateType].styles, "styles");
                    if (styles[this.json.templateType].inputStyles) this.removeStyles(styles[this.json.templateType].inputStyles, "inputStyles");
                    if (styles[this.json.templateType].properties) this.removeStyles(styles[this.json.templateType].properties, "properties");
                }
            }
        }
    },

    setTemplateStyles: function(styles){
        if (this.json.templateType){
            if (styles[this.json.templateType]){
                if (styles[this.json.templateType].styles) this.copyStyles(styles[this.json.templateType].styles, "styles");
                if (styles[this.json.templateType].properties) this.copyStyles(styles[this.json.templateType].properties, "properties");
            }
        }
    },
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "div",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
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
                        if (moduleStyles[this.json.templateType].styles) this.copyStyles(moduleStyles[this.json.templateType].styles, "styles");
                        if (moduleStyles[this.json.templateType].styles) this.copyStyles(moduleStyles[this.json.templateType].properties, "properties");
                    }

                    this.setPropertiesOrStyles("styles");
                    this.setPropertiesOrStyles("properties");

                    this.reloadMaplist();
                }
            }
        }
	}
	
});
