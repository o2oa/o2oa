MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.$Input = MWF.FC$Input = new Class({
    Extends: MWF.FC$Element,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "type": "textfield",
        "path": "../x_component_process_FormDesigner/Module/Textfield/",
        "propertyPath": "../x_component_process_FormDesigner/Module/Textfield/textfield.html"
    },

    initialize: function(form, options){
        this.setOptions(options);

        this.path = this.options.path;
        this.cssPath = this.path+this.options.style+"/css.wcss";

        this._loadCss();
        this.moduleType = "element";
        this.moduleName = this.options.type;

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

    _resetModuleDomNode: function(){
        if (this.json.preprocessing){
            this.node.empty();
            var icon = new Element("div", {
                "styles": this.css.textfieldIcon
            }).inject(this.node);
            var text = new Element("div", {
                "styles": this.css.moduleText,
                "text": this.json.id
            }).inject(this.node);
        }
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
            //         //if (!this.json.styles.width) this.json.styles.width = "90%";
            //     }
            // }
            try{
                this.setCustomStyles();
            }catch(e){}
            //this.setCustomStyles();
        }
        if (name=="inputStyles"){
            try{
                this.setCustomInputStyles();
            }catch(e){}
            //
            // var text = this.node.getLast("div");
            // text.clearStyles();
            // text.setStyles(this.css.moduleText);
            //
            // Object.each(this.json.inputStyles, function(value, key){
            //     var reg = /^border\w*/ig;
            //     if (!key.test(reg)){
            //         text.setStyle(key, value);
            //     }
            // }.bind(this));
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
    },
    _preprocessingModuleData: function(){
        debugger;
        this.node.clearStyles();
        this.recoveryIconNode = this.node.getFirst();
        this.recoveryIconNode.dispose();
        this.recoveryTextNode = this.node.getFirst();
        this.recoveryTextNode.dispose();

        var inputNode = new Element("input", {
            "styles": {
                "background": "transparent",
                "width": "100%",
                "border": "0px"
            }
        }).inject(this.node);
        this.node.setStyles({
            "overflow": "hidden",
            "position": "relative",
            "margin-right": "20px",
            "padding-right": "4px"
        });

        if (this.json.styles){
            this.json.recoveryStyles = Object.clone(this.json.styles);
            if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
                if ((value.indexOf("x_processplatform_assemble_surface")==-1 && value.indexOf("x_portal_assemble_surface")==-1)){
                    this.node.setStyle(key, value);
                    delete this.json.styles[key];
                }
            }.bind(this));
        }
        if (this.json.inputStyles){
            this.json.recoveryInputStyles = Object.clone(this.json.inputStyles);
            var inputNode = this.node.getFirst();
            if (inputNode){
                if (this.json.recoveryInputStyles) Object.each(this.json.recoveryInputStyles, function(value, key){
                    if ((value.indexOf("x_processplatform_assemble_surface")==-1 && value.indexOf("x_portal_assemble_surface")==-1)){
                        inputNode.setStyle(key, value);
                        delete this.json.inputStyles[key];
                    }
                }.bind(this));
            }
        }
        this.json.preprocessing = "y";
    },
    _recoveryModuleData: function(){
        if (this.json.recoveryStyles) this.json.styles = this.json.recoveryStyles;
        if (this.json.recoveryInputStyles) this.json.inputStyles = this.json.recoveryInputStyles;

        if (this.recoveryTextNode) {
            this.node.empty();
            this.recoveryTextNode.inject(this.node, "top");
        }
        if (this.recoveryIconNode) {
            this.recoveryIconNode.inject(this.node, "top");
        }

        this.json.recoveryStyles = null;
        this.json.recoveryInputStyles = null;
        this.recoveryIconNode = null;
        this.recoveryTextNode = null;
    },

    setCustomStyles: function(){
        this._recoveryModuleData();
        //debugger;
        //var border = this.node.getStyle("border");
        this.node.clearStyles();
        this.node.setStyles(this.css.moduleNode);

        if (this.initialStyles) this.node.setStyles(this.initialStyles);
        //this.node.setStyle("border", border);

        if (this.json.styles) Object.each(this.json.styles, function(value, key){
            if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
                    value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
                    value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }
                if (value.indexOf("/x_portal_assemble_surface")!==-1){
                    value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (value.indexOf("x_portal_assemble_surface")!==-1){
                    value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
                value = o2.filterUrl(value);
            }

            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                if (key){
                    if (key.toString().toLowerCase()==="display"){
                        if (value.toString().toLowerCase()==="none"){
                            this.node.setStyle("opacity", 0.3);
                        }else{
                            this.node.setStyle("opacity", 1);
                            this.node.setStyle(key, value);
                        }
                    }else{
                        this.node.setStyle(key, value);
                    }
                }
            }
        }.bind(this));
    },
    setCustomInputStyles: function(){
        this._recoveryModuleData();

        var inputNode = this.node.getLast();
        if (inputNode){
            inputNode.clearStyles();
            inputNode.setStyles(this.css.moduleText);

            if (this.json.inputStyles) Object.each(this.json.inputStyles, function(value, key){
                if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
                    var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                    var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                    if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
                        value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                    }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
                        value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                    }
                    if (value.indexOf("/x_portal_assemble_surface")!==-1){
                        value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                    }else if (value.indexOf("x_portal_assemble_surface")!==-1){
                        value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                    }
                    value = o2.filterUrl(value);
                }

                var reg = /^border\w*/ig;
                if (!key.test(reg)){
                    if (key){
                        if (key.toString().toLowerCase()==="display"){
                            if (value.toString().toLowerCase()==="none"){
                                inputNode.setStyle("opacity", 0.3);
                            }else{
                                inputNode.setStyle("opacity", 1);
                                inputNode.setStyle(key, value);
                            }
                        }else{
                            inputNode.setStyle(key, value);
                        }
                    }
                }
            }.bind(this));
        }
    }
});
