MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Label = MWF.FCLabel = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Label/label.html"
	},
	
	initialize: function(form, options){
        debugger;
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Label/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Label/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "label";

		this.form = form;
		this.container = null;
		this.containerNode = null;
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
            if (styles[this.json.templateType].styles) this.copyStyles(styles[this.json.templateType].styles, "styles");
            if (styles[this.json.templateType].properties) this.copyStyles(styles[this.json.templateType].properties, "properties");
        }
    },
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "label",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"text": "(T)Text",
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	
	_setEditStyle_custom: function(name, obj, oldValue){
        debugger;
		if (name=="valueType" || name=="text"){
			if (this.json.valueType=="text"){
				if (this.json.text){
					this.node.set("text", this.json.text);
				}else{
					this.node.set("text", "(T)Text");
				} 
			}else{
				this.node.set("text", "(C)Text");
			}
		}
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

                    if (moduleStyles[this.json.templateType].styles) this.copyStyles(moduleStyles[this.json.templateType].styles, "styles");
                    if (moduleStyles[this.json.templateType].styles) this.copyStyles(moduleStyles[this.json.templateType].properties, "properties");

                    this.setPropertiesOrStyles("styles");
                    this.setPropertiesOrStyles("properties");

                    this.reloadMaplist();
                }
            }
        }
	}
});
