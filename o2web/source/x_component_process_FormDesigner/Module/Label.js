MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Label = MWF.FCLabel = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Label/label.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Label/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Label/"+this.options.style+"/css.wcss";

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
            if (styles[this.json.templateType]){
                if (styles[this.json.templateType].styles) this.copyStyles(styles[this.json.templateType].styles, "styles");
                if (styles[this.json.templateType].properties) this.copyStyles(styles[this.json.templateType].properties, "properties");
            }
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
    _setNodeProperty: function(){
        if (this.form.moduleList.indexOf(this)==-1) this.form.moduleList.push(this);
        if (this.form.moduleNodeList.indexOf(this.node)==-1) this.form.moduleNodeList.push(this.node);
        if (this.form.moduleElementNodeList.indexOf(this.node)==-1) this.form.moduleElementNodeList.push(this.node);
        this.node.store("module", this);
		this.setPrefixOrSuffix();
	},
	_setEditStyle_custom: function(name, obj, oldValue){
		if (name=="valueType" || name=="text"){
			if (this.json.valueType=="text"){
				if (this.json.text){
					if (this.textNode){
                        this.textNode.set("text", this.json.text);
					}else{
                        this.node.set("text", this.json.text);
					}

				}else{
                    if (this.textNode){
                        this.textNode.set("text", "(T)Text");
                    }else{
                        this.node.set("text", "(T)Text");
                    }
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
        if (name=="prefixIcon" || name=="suffixIcon"){
            this.setPrefixOrSuffix();
		}
        if (name=="styles"){
            this.resetPrefixOrSuffix();
		}
	},
    resetPrefixOrSuffix: function(){
		if (this.prefixNode){
            var y = this.textNode.getSize().y;
            this.prefixNode.setStyle("height", ""+y+"px");
		}
        if (this.suffixNode){
            var y = this.textNode.getSize().y;
            this.suffixNode.setStyle("height", ""+y+"px");
        }
	},

	setPrefixOrSuffix: function(){
        if (this.json.prefixIcon || this.json.suffixIcon){
        	if (!this.textNode){
                var text = this.node.get("text");
                this.node.empty();

                this.textNode = new Element("div", {"styles": {
                    "margin-left": (this.json.prefixIcon) ? "20px" : "0px",
                    "margin-right": (this.json.suffixIcon) ? "20px" : "0px",
                    "overflow": "hidden"
                }, "text": text}).inject(this.node);

                if (this.json.prefixIcon){
                    this.prefixNode = new Element("div", {"styles": {
                        "float": "left",
                        "width": "20px",
                        "height": ""+this.node.getSize().y+"px",
                        "background": "url("+this.json.prefixIcon+") center center no-repeat"
                    }}).inject(this.textNode, "before");
                }
                if (this.json.suffixIcon){
                    this.suffixNode = new Element("div", {"styles": {
                        "float": "right",
                        "width": "20px",
                        "height": ""+this.node.getSize().y+"px",
                        "background": "url("+this.json.suffixIcon+") center center no-repeat"
                    }}).inject(this.textNode, "before");
                }
			}else{
                if (this.json.prefixIcon){
                	if (!this.prefixNode){
                        this.prefixNode = new Element("div", {"styles": {
                            "float": "left",
                            "width": "20px",
                            "height": ""+this.node.getSize().y+"px",
                            "background": "url("+this.json.prefixIcon+") center center no-repeat"
                        }}).inject(this.textNode, "before");
					}else{
                        this.prefixNode.setStyle("background", "url("+this.json.prefixIcon+") center center no-repeat");
					}
				}else{
                    if (this.prefixNode){
                        this.prefixNode.destroy();
                        this.prefixNode = null;
					}
				}
                if (this.json.suffixIcon){
                    if (!this.suffixNode){
                        this.suffixNode = new Element("div", {"styles": {
                            "float": "right",
                            "width": "20px",
                            "height": ""+this.node.getSize().y+"px",
                            "background": "url("+this.json.suffixIcon+") center center no-repeat"
                        }}).inject(this.textNode, "before");
                    }else{
                        this.suffixNode.setStyle("background", "url("+this.json.suffixIcon+") center center no-repeat");
                    }
                }else{
                    if (this.suffixNode){
                        this.suffixNode.destroy();
                        this.suffixNode = null;
                    }
                }
			}

        }else{
        	//var text = this.textNode.get("text");
        	this.node.empty();
        	if (this.json.valueType=="text"){
                this.node.set("text", this.json.text || "(T)Text");
            }else{
                this.node.set("text", this.json.text || "(C)Text");
            }
			this.prefixNode = null;
            this.suffixNode = null;
		}
	},
    _preprocessingModuleData: function(){
        this.node.clearStyles();
        this.json.recoveryStyles = Object.clone(this.json.styles);

        if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
            if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
                //需要运行时处理
            }else{
                this.node.setStyle(key, value);
                delete this.json.styles[key];
            }
        }.bind(this));

        if (this.json.valueType==="text"){
            if (!this.json.text){
                if (this.textNode){
                    this.textNode.set("text", "");
                }else{
                    this.node.set("text", "");
                }
            }
        }else{
            this.node.set("text", "");
        }
        this.json.preprocessing = "y";
    },
    _recoveryModuleData: function(){
        if (this.json.recoveryStyles) this.json.styles = this.json.recoveryStyles;
        this.json.recoveryStyles = null;
        this._setEditStyle_custom("text");
    }

});
