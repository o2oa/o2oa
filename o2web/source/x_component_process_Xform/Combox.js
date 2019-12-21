MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
MWF.xApplication.process.Xform.Combox = MWF.APPCombox =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Input,
	iconStyle: "selectIcon",
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad", "commitInput", "change"]
    },

    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
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
        //new Element("select").inject(this.node);
    },
    _loadNodeEdit: function(){
        this.node.empty();

        MWF.require("MWF.widget.Combox", function(){
            this.combox = select = new MWF.widget.Combox({
                "onlySelect": this.json.onlySelect==="y",
                "count": this.json.count.toInt() || 0,
                "splitStr": this.json.splitStr || ",\\s*|;\\s*|，\\s*|；\\s*",
                "splitShow": this.json.splitShow || ", ",
                "list": this.getOptions(),
                "onCommitInput": function(item){
                    this.fireEvent("commitInput");
                }.bind(this),
                "onChange": function(){
                    this.fireEvent("change");
                }.bind(this),
                "optionsMethod": this._searchOptions()
            });
        }.bind(this), false);


        // var select = new Element("select");
        // select.set(this.json.properties);
        select.inject(this.node);
        //this.node.destroy();
        // this.areaNode = this.node;
        // this.node = select;
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });

        this.combox.addEvent("change", function(){
            this.validationMode();
            if (this.validation()) this._setBusinessData(this.getInputData("change"));
        }.bind(this));

    },
    _searchOptions: function(){
        if (this.json.itemType === "dynamic"){
			return function(value, callback){
				var event = {
					"value": value,
					"callback": callback
				};
                this.form.Macro.fire(this.json.itemDynamic.code, this, event);
			}.bind(this)
		}else{
        	return null;
		}
	},
    getOptions: function(){
    	var list = [];
        if (this.json.itemType === "values"){
            list = this.json.itemValues;
        }else if (this.json.itemType === "script"){
            list = this.form.Macro.exec(this.json.itemScript.code, this);
        }

        if (list.length){
            var options = [];
            list.each(function(v){
                if (typeOf(v)==="object"){
                    options.push(v);
                }else{
                	v = v.toString();
                	arr = v.split("|");
                	var o = { "text": "", "keyword": "", "value": "" };
                	switch (arr.length){
                        case 0: break;
						case 1:
                            o.text = arr[0];
                            o.keyword = arr[0];
                            o.value = arr[0];
							break;
						case 2:
                            o.text = arr[0];
                            o.keyword = arr[0];
                            o.value = arr[1];
							break;
						case 3:
                            o.text = arr[0];
                            o.keyword = arr[1];
                            o.value = arr[2];
							break;
						default:
                            o.text = arr[0];
                            o.keyword = arr[1];
                            o.value = arr[2];
					}
                    options.push(o);
				}
			}.bind(this));
            return options;
		}
        return [];
    },
    setData: function(value){
        this._setBusinessData(value);
        this._setValue(value);
	},
    _setValue: function(value){
        if (!value) value = [];
        if (value.length==1 && (!value[0])) value = [];
        if (typeOf(value) !=="array") value = [value];

		if (this.combox){
            this.combox.clear();

            comboxValues = [];
            value.each(function(v){
				if (typeOf(v)==="object"){
                    comboxValues.push({
						"text": v.text || v.title || v.subject  || v.name,
						"value": v
                    });
				}else{
                    comboxValues.push(v.toString());
				}
			}.bind(this));
            this.combox.addNewValues(comboxValues);
		}else{
            value.each(function(v, i){
                var text = "";
                if (typeOf(v)==="object"){
                	text = v.text || v.title || v.subject  || v.name;
                }else{
                    text = v.toString();
                }
                if (i<value.length-1) text += this.json.splitShow;
                new Element("div", {"styles": {
                    "float": "left",
                    "margin-right": "5px"
                },"text": text}).inject(this.node.getFirst() || this.node);
            }.bind(this));
		}
    },
    resetOption: function(){
        if (this.combox){
            var list = this.getOptions();
            this.combox.setOptions({"list": list});
        }
    },

	addOption: function(text, value){
        if (this.combox){
            var list = this.getOptions();
            list.push({
				"text": text,
				"value": value
			});
            this.combox.setOptions({"list": list});
        }
	},
    getInputData: function(){
        if (this.combox) return this.combox.getData();
        return this._getBusinessData();
    },
    getTextData: function(){
	    var v = this.getData();
        return {"value": v, "text": v};
        //return this.node.get("text");
    },
    resetData: function(){
        this.setData(this.getValue());
    }
	
}); 