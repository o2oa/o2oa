MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
MWF.xApplication.process.Xform.Checkbox = MWF.APPCheckbox =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Input,

    loadDescription: function(){},
    _loadNode: function(){
        if (this.readonly || this.json.isReadonly ){
            this._loadNodeRead();
        }else{
            this._loadNodeEdit();
        }
    },
    _loadNodeRead: function(){
        this.node.empty();
        var radioValues = this.getOptions();
        var value = this.getValue();
        if (value){
            var texts = [];
            radioValues.each(function(item){
                var tmps = item.split("|");
                var t = tmps[0];
                var v = tmps[1] || t;

                if (value.indexOf(v)!=-1){
                    texts.push(t);
                }
            });
            this.node.set("text", texts.join(", "));
        }
    },

    _loadNodeEdit: function(){
		//this.container = new Element("select");
		var div = new Element("div");
		div.set(this.json.properties);
		div.inject(this.node, "after");
	
		this.node.destroy();
		this.node = div;
		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type,
			"styles": {
				"display": "inline"
			}
		});
		this.setOptions();
	},
    _loadDomEvents: function(){
    },
    _loadEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!=-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    //this.node.addEvent(key, function(event){
                    //    return this.form.Macro.fire(e.code, this, event);
                    //}.bind(this));
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
            var inputs = this.node.getElements("input");
            inputs.each(function(input){
                input.addEvent(key, function(event){
                    return (fun) ? fun(this, event) : null;
                }.bind(this));
            }.bind(this));
        }
    },
    resetOption: function(){
        this.node.empty();
        this.setOptions();
    },
	getOptions: function(){
		if (this.json.itemType == "values"){
			return this.json.itemValues;
		}else{
			return this.form.Macro.exec(this.json.itemScript.code, this);
		}
		//return [];
	},
	
	setOptions: function(){
		var radioValues = this.getOptions();
        if (!radioValues) radioValues = [];
        if (o2.typeOf(radioValues)==="array"){
            var flag = (new MWF.widget.UUID).toString();
            radioValues.each(function(item){
                var tmps = item.split("|");
                var text = tmps[0];
                var value = tmps[1] || text;

                var radio = new Element("input", {
                    "type": "checkbox",
                    "name": this.json.properties.name || flag+this.json.id,
                    "value": value,
                    "showText": text,
                    "styles": this.json.buttonStyles
                }).inject(this.node);
                //radio.appendText(text, "after");

                var textNode = new Element( "span", {
                    "text" : text,
                    "styles" : { "cursor" : "default" }
                }).inject(this.node);
                textNode.addEvent("click", function( ev ){
                    if( this.radio.get("disabled") === true || this.radio.get("disabled") === "true" )return;
                    this.radio.checked = ! this.radio.checked;
                    this.radio.fireEvent("change");
                    this.radio.fireEvent("click");
                }.bind( {radio : radio} ) );

                radio.addEvent("click", function(){
                    this.validationMode();
                    if (this.validation()) this._setBusinessData(this.getInputData("change"));
                }.bind(this));

                Object.each(this.json.events, function(e, key){
                    if (e.code){
                        if (this.options.moduleEvents.indexOf(key)!=-1){
                        }else{
                            radio.addEvent(key, function(event){
                                return this.form.Macro.fire(e.code, this, event);
                            }.bind(this));
                        }
                    }
                }.bind(this));

            }.bind(this));
        }
	},
    _setValue: function(value){
        this._setBusinessData(value);
        var radios = this.node.getElements("input");
        for (var i=0; i<radios.length; i++){
            var radio = radios[i];
            radio.checked = value.indexOf(radio.value) != -1;
        }
    },
	getTextData: function(){
		var inputs = this.node.getElements("input");
		var value = [];
		var text = [];
		if (inputs.length){
			inputs.each(function(input){
				if (input.checked){
					var v = input.get("value");
					var t = input.get("showText");
					value.push(v || "");
					text.push(t || v || "");
				}
			});
		}
		if (!value.length) value = [""];
		if (!text.length) text = [""];
		return {"value": value, "text": text};
	},
    //getData: function(){
		//var inputs = this.node.getElements("input");
		//var value = [];
		//if (inputs.length){
		//	inputs.each(function(input){
		//		if (input.checked){
		//			var v = input.get("value");
		//			if (v) value.push(v || "");
		//		}
		//	});
		//}
		//return (value.length==1) ? value[0] : value;
    //},
    getInputData: function(){
        var inputs = this.node.getElements("input");
        var value = [];
        if (inputs.length){
            inputs.each(function(input){
                if (input.checked){
                    var v = input.get("value");
                    if (v) value.push(v || "");
                }
            });
        }
        return (value.length) ? value : null;
    },
    resetData: function(){
        this.setData(this.getValue());
    },
	setData: function(data){
        this._setBusinessData(data);

		var inputs = this.node.getElements("input");
		if (inputs.length){
			inputs.each(function(input){
				if (typeOf(data)=="array"){
					if (data.indexOf(input.get("value"))!=-1){
						input.set("checked", true);
					}else{
						input.set("checked", false);
					}
				}else{
					if (data == input.get("value")){
						input.set("checked", true);
					}else{
						input.set("checked", false);
					}
				}
			});
		}
        this.fireEvent("setData");
	},

    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            this.isNotValidationMode = true;
            this.node.store("background", this.node.getStyles("background"));
            this.node.setStyle("background", "#ffdcdc");

            this.errNode = this.createErrorNode(text);
            if (this.iconNode){
                this.errNode.inject(this.iconNode, "after");
            }else{
                this.errNode.inject(this.node, "after");
            }
            this.showNotValidationMode(this.node);

            if (!this.node.isIntoView()) this.node.scrollIntoView();
        }
    },
    validationMode: function(){
        if (this.isNotValidationMode){
            this.isNotValidationMode = false;
            this.node.setStyles(this.node.retrieve("background"));
            if (this.errNode){
                this.errNode.destroy();
                this.errNode = null;
            }
        }
    }
	
}); 