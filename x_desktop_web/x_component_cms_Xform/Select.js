MWF.xDesktop.requireApp("cms.Xform", "$Input", null, false);
MWF.xApplication.cms.Xform.Select = MWF.CMSSelect =  new Class({
	Implements: [Events],
	Extends: MWF.CMS$Input,
	iconStyle: "selectIcon",

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
        var optionItems = this.getOptions();
        var value = this.getValue();
        if (value){
            var texts = [];
            optionItems.each(function(item){
                var tmps = item.split("|");
                var t = tmps[0];
                var v = tmps[1] || t;

                if (v){
                    if (value.indexOf(v)!=-1){
                        texts.push(t);
                    }
                }

            });
            this.node.set("text", texts.join(", "));
        }
    },

    _loadNodeEdit: function(){
		var select = new Element("select");
		select.set(this.json.properties);
		select.inject(this.node, "after");
		this.node.destroy();
		this.node = select;
		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type,
			"styles": {
				"margin-right": "12px"
			}
		});
		
		this.setOptions();
        this.node.addEvent("change", function(){
            this.validationMode();
            if (this.validation()) this._setBusinessData(this.getInputData("change"));
        }.bind(this));

	},
	getOptions: function(){
		if (this.json.itemType == "values"){
			return this.json.itemValues;
		}else{
			return this.form.CMSMacro.exec(this.json.itemScript.code, this);
		}
		return [];
	},
	setOptions: function(){
		var optionItems = this.getOptions();
		optionItems.each(function(item){
			var tmps = item.split("|");
			var text = tmps[0];
			var value = tmps[1] || text;

			var option = new Element("option", {
				"value": value,
				"text": text
			}).inject(this.node);
		}.bind(this));
	},
	_setValue: function(value){
        if (!this.readonly) {
            this._setBusinessData(value);
            for (var i=0; i<this.node.options.length; i++){
                var option = this.node.options[i];
                if (option.value==value){
                    option.selected = true;
                    //	break;
                }else{
                    option.selected = false;
                }
            }
        }
		//this.node.set("value", value);
	},
	getTextData: function(){
		var ops = this.node.getElements("option");
		var value = [];
		var text = [];
		ops.each(function(op){
			if (op.selected){
				var v = op.get("value");
				var t = op.get("text");
				value.push(v || "");
				text.push(t || v || "");
			}
		});
		if (!value.length) value = [""];
		if (!text.length) text = [""];
		return {"value": value, "text": text};
	},
    getInputData: function(){
		var ops = this.node.getElements("option");
		var value = [];
		ops.each(function(op){
			if (op.selected){
				var v = op.get("value");
				if (v) value.push(v);
			}
		});
        if (!value.length) return null;
		return (value.length==1) ? value[0] : value;
	},
    resetData: function(){
        this.setData(this.getValue());
    },
	setData: function(data){
        this._setBusinessData(data);
		var ops = this.node.getElements("option");
		
		ops.each(function(op){
			if (typeOf(data)=="array"){
				if (data.indexOf(op.get("value"))!=-1){
					op.set("selected", true);
				}else{
					op.set("selected", false);
				}
			}else{
				if (data == op.get("value")){
					op.set("selected", true);
				}else{
					op.set("selected", false);
				}
			}
		});
	}
	
}); 