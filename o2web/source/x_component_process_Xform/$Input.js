MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.$Input = MWF.APP$Input =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Module,
	iconStyle: "personfieldIcon",

    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
    },
	
	_loadUserInterface: function(){
		this._loadNode();
        if (this.json.compute === "show"){
            this._setValue(this._computeValue());
        }else{
            this._loadValue();
        }
	},
    _loadDomEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1){
                    (this.node.getFirst() || this.node).addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    (this.node.getFirst() || this.node).addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    addModuleEvent: function(key, fun){
        if (this.options.moduleEvents.indexOf(key)!==-1){
            this.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }else{
            (this.node.getFirst() || this.node).addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }
    },

    _loadNode: function(){
        if (this.readonly){
            this._loadNodeRead();
        }else{
            this._loadNodeEdit();
        }
    },
    _loadNodeRead: function(){
        this.node.empty();
    },
    loadDescription: function(){
        var v = this._getBusinessData();
        if (!v){
            if (this.json.description){
                var size = this.node.getFirst().getSize();
                var w = size.x-3;
                if (COMMON.Browser.safari) w = w-20;
                this.descriptionNode = new Element("div", {"styles": this.form.css.descriptionNode, "text": this.json.description}).inject(this.node);
                this.descriptionNode.setStyles({
                    "width": ""+w+"px",
                    "height": ""+size.y+"px",
                    "line-height": ""+size.y+"px"
                });
                this.setDescriptionEvent();
            }
        }
    },
    setDescriptionEvent: function(){
        if (this.descriptionNode){
            this.descriptionNode.addEvents({
                "mousedown": function(){
                    this.descriptionNode.setStyle("display", "none");
                    this.clickSelect();
                }.bind(this)
            });
            this.node.getFirst().addEvents({
                "focus": function(){
                    if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
                }.bind(this),
                "blur": function(){
                    if (!this.node.getFirst().get("value")) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
                }.bind(this)
            });
        }
    },
    checkDescription: function(){
        if (!this.node.getFirst().get("value")){
            if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
        }else{
            if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
        }
    },
    _loadNodeEdit: function(){
		var input = new Element("input", {
            "styles": {
                "background": "transparent",
                "width": "100%",
                "border": "0px"
            },
            "readonly": true
        });
		input.set(this.json.properties);

        var node = new Element("div", {"styles": {
            "overflow": "hidden",
            "position": "relative",
            "margin-right": "20px",
            "padding-right": "4px"
        }}).inject(this.node, "after");
        input.inject(node);

        this.node.destroy();
		this.node = node;
		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type,
			"readonly": true,
			"events": {
				"click": this.clickSelect.bind(this)
			}
		});
        if (this.json.showIcon!='no' && !this.form.json.hideModuleIcon ){
            this.iconNode = new Element("div", {
                "styles": this.form.css[this.iconStyle],
                "events": {
                    "click": this.clickSelect.bind(this)
                }
            }).inject(this.node, "before");
        }else if( this.form.json.nodeStyleWithhideModuleIcon ){
            this.node.setStyles(this.form.json.nodeStyleWithhideModuleIcon)
        }

        this.node.getFirst().addEvent("change", function(){
            this.validationMode();
            if (this.validation()) this._setBusinessData(this.getInputData("change"));
        }.bind(this));
	},
    _loadStyles: function(){
        if (this.json.styles) this.node.setStyles(this.json.styles);
        if (this.json.inputStyles) if (this.node.getFirst()) this.node.getFirst().setStyles(this.json.inputStyles);
        if (this.iconNode){
            var size = this.node.getSize();
            //if (!size.y){
            //    var y1 = this.node.getStyle("height");
            //    var y2 = this.node.getFirst().getStyle("height");
            //    alert(y1+"," +y2);
            //    var y = ((y1!="auto" && y1>y2) || y2=="auto") ? y1 : y2;
            //    size.y = (y=="auto") ? "auto" : y.toInt();
            //    //alert(size.y)
            //}
            this.iconNode.setStyle("height", ""+size.y+"px");
            //alert(this.iconNode.getStyle("height"))
        }
    },
    _computeValue: function(value){
        return (this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
    },
	getValue: function(){
        var value = this._getBusinessData();
        if (!value) value = this._computeValue();
		return value || "";
	},
    _setValue: function(value){
        this._setBusinessData(value);
        if (this.node.getFirst()) this.node.getFirst().set("value", value || "");
        if (this.readonly || this.json.isReadonly) this.node.set("text", value);
    },
	_loadValue: function(){
        this._setValue(this.getValue());
	},
	clickSelect: function(){
	},
	_afterLoaded: function(){
//		if (this.iconNode){
////			var p = this.node.getPosition();
////			var s = this.node.getSize();
////			var is = this.iconNode.getSize();
////
////			var y = p.y;
////			var x = p.x+s.x-is.x;
//			this.iconNode.setStyles({
//				"top": "5px",
//				"left": "-18px"
//			});
//		}
        if (!this.readonly){
            this.loadDescription();
        }
	},
	
	getTextData: function(){
		//var value = this.node.get("value");
		//var text = this.node.get("text");
        var value = (this.node.getFirst()) ? this.node.getFirst().get("value") : this.node.get("text");
        var text = (this.node.getFirst()) ? this.node.getFirst().get("text") : this.node.get("text");
		return {"value": [value || ""] , "text": [text || value || ""]};
	},
	getData: function(when){
        if (this.json.compute == "save") this._setValue(this._computeValue());
		return this.getInputData();
	},
    getInputData: function(){
        if (this.node.getFirst()){
            return this.node.getFirst().get("value");
        }else{
            return this._getBusinessData();
        }
    },
    resetData: function(){
        this.setData(this.getValue());
    },
	setData: function(data){
        this._setBusinessData(data);
		if (this.node.getFirst()){
            this.node.getFirst().set("value", data);
            this.checkDescription();
            this.validationMode();
        }else{
            this.node.set("text", data);
        }
	},

    createErrorNode: function(text){

        //var size = this.node.getFirst().getSize();
        //var w = size.x-3;
        //if (COMMON.Browser.safari) w = w-20;
        //node.setStyles({
        //    "width": ""+w+"px",
        //    "height": ""+size.y+"px",
        //    "line-height": ""+size.y+"px",
        //    "position": "absolute",
        //    "top": "0px"
        //});
        var node;
        if( this.form.json.errorStyle ){
            node = new Element("div",{
                "styles" : this.form.json.errorStyle.node,
                "text": text
            });
            if( this.form.json.errorStyle.close ){
                var closeNode = new Element("div",{
                    "styles" : this.form.json.errorStyle.close ,
                    "events": {
                        "click" : function(){
                            this.destroy();
                        }.bind(node)
                    }
                }).inject(node);
            }
        }else{
            node = new Element("div");
            var iconNode = new Element("div", {
                "styles": {
                    "width": "20px",
                    "height": "20px",
                    "float": "left",
                    "background": "url("+"/x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
                }
            }).inject(node);
            var textNode = new Element("div", {
                "styles": {
                    "height": "20px",
                    "line-height": "20px",
                    "margin-left": "20px",
                    "color": "red",
                    "word-break": "keep-all"
                },
                "text": text
            }).inject(node);
        }
        return node;
    },
    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            debugger;
            this.isNotValidationMode = true;
            this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            this.node.setStyle("border-color", "red");

            this.errNode = this.createErrorNode(text);
            //if (this.iconNode){
            //    this.errNode.inject(this.iconNode, "after");
            //}else{
                this.errNode.inject(this.node, "after");
            //}
            this.showNotValidationMode(this.node);

            var parentNode = this.node;
            while( parentNode.offsetParent === null ){
                parentNode = parentNode.getParent();
            }

            if (!parentNode.isIntoView()) parentNode.scrollIntoView();
        }
    },
    showNotValidationMode: function(node){
        var p = node.getParent("div");
        if (p){
            var mwftype = p.get("MWFtype") || p.get("mwftype");
            if (mwftype == "tab$Content"){
                if (p.getParent("div").getStyle("display")=="none"){
                    var contentAreaNode = p.getParent("div").getParent("div");
                    var tabAreaNode = contentAreaNode.getPrevious("div");
                    var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
                    var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
                    tabNode.click();
                    p = tabAreaNode.getParent("div");
                }
            }
            this.showNotValidationMode(p);
        }
    },
    validationMode: function(){
        if (this.isNotValidationMode){
            this.isNotValidationMode = false;
            this.node.setStyles(this.node.retrieve("borderStyle"));
            if (this.errNode){
                this.errNode.destroy();
                this.errNode = null;
            }
        }
    },
    validationConfigItem: function(routeName, data){
        var flag = (data.status==="all") ? true: (routeName === data.decision);
        if (flag){
            var n = this.getInputData();
            var v = (data.valueType==="value") ? n : n.length;
            switch (data.operateor){
                case "isnull":
                    if (!v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    },
    validationConfig: function(routeName, opinion){
        if (this.json.validationConfig){
            if (this.json.validationConfig.length){
                for (var i=0; i<this.json.validationConfig.length; i++) {
                    var data = this.json.validationConfig[i];
                    if (!this.validationConfigItem(routeName, data)) return false;
                }
            }
            return true;
        }
        return true;
    },
    validation: function(routeName, opinion){
        if (!this.readonly && !this.json.isReadonly){
            if (!this.validationConfig(routeName, opinion))  return false;

            if (!this.json.validation) return true;
            if (!this.json.validation.code) return true;
            var flag = this.form.Macro.exec(this.json.validation.code, this);
            if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
            if (flag.toString()!="true"){
                this.notValidationMode(flag);
                return false;
            }
        }
        return true;
    }
	
}); 