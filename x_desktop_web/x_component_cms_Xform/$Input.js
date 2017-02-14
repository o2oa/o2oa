MWF.xDesktop.requireApp("cms.Xform", "$Module", null, false);
MWF.xApplication.cms.Xform.$Input = MWF.CMS$Input =  new Class({
	Implements: [Events],
	Extends: MWF.CMS$Module,
    styles : "textfieldNode",
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

        if (this.json.compute == "show"){
            this._setValue(this._computeValue());
        }else{
            this._loadValue();
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
    _loadNodeEdit: function(){
		var input = new Element("input");
		input.set(this.json.properties);
		input.inject(this.node, "after");
		this.node.destroy();
		this.node = input;
		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type,
			"readonly": true,
			"events": {
				"click": this.clickSelect.bind(this)
			}
		});

        var styles = this.form.css[this.styles];
        if( styles )this.node.setStyles( styles );

        var styles_select = this.form.css[this.styles+ "_select"];
        if( styles && styles_select ){
            this.node.addEvents( { "focus" : function(){
                this.node.setStyles( this.form.css[this.styles+ "_select"] )
            }.bind(this),
                "blur" : function(){
                    this.node.setStyles( this.form.css[this.styles] )
                }.bind(this)
            });
        }

		this.iconNode = new Element("div", {
			"styles": this.form.css[this.iconStyle],
			"events": {
				"click": this.clickSelect.bind(this)
			}
		}).inject(this.node, "after");

        this.node.addEvent("change", function(){
            this.validationMode();
            if (this.validation()) this._setBusinessData(this.getInputData("change"));
        }.bind(this));
	},
    _computeValue: function(value){
        return (this.json.defaultValue.code) ? this.form.CMSMacro.exec(this.json.defaultValue.code, this): (value || "");
    },
	getValue: function(){
        var value = this._getBusinessData();
        if (!value) value = this._computeValue();
		return value || "";
	},
    _setValue: function(value){
        this._setBusinessData(value);
        this.node.set("value", value);
        if (this.readonly) this.node.set("text", value);
    },
	_loadValue: function(){
        this._setValue(this.getValue());
	},
	clickSelect: function(){
	},
	_afterLoaded: function(){
		if (this.iconNode){
//			var p = this.node.getPosition();
//			var s = this.node.getSize();
//			var is = this.iconNode.getSize();
//			
//			var y = p.y;
//			var x = p.x+s.x-is.x;
//			this.iconNode.setStyles({
//
//			});
		}
	},
	
	getTextData: function(){
		var value = this.node.get("value");
		var text = this.node.get("text");
		return {"value": [value] || "", "text": [text || value || ""]};
	},
	getData: function(when){
        if (this.json.compute == "save") this._setValue(this._computeValue());
		return this.getInputData();
	},
    getInputData: function(){
        return this.node.get("value");
    },
    resetData: function(){
        this.setData(this.getValue());
    },
	setData: function(data){
        this._setBusinessData(data);
		this.node.set("value", data);
	},

    createErrorNode: function(text){
        var node = new Element("div");
        var iconNode = new Element("div", {
            "styles": {
                "width": "20px",
                "height": "20px",
                "float": "left",
                "background": "url("+"/x_component_cms_Xform/$Form/default/icon/error.png) center center no-repeat"
            }
        }).inject(node);
        var textNode = new Element("div", {
            "styles": {
                "line-height": "20px",
                "margin-left": "20px",
                "color": "red"
            },
            "text": text
        }).inject(node);
        return node;
    },
    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            this.isNotValidationMode = true;
            this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            this.node.setStyle("border", "1px solid red");

            this.errNode = this.createErrorNode(text)
            if (this.iconNode){
                this.errNode.inject(this.iconNode, "after");
            }else{
                this.errNode.inject(this.node, "after");
            }

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
    validation: function(){
        if (!this.json.validation) return true;
        if (!this.json.validation.code) return true;
        var flag = this.form.CMSMacro.exec(this.json.validation.code, this);
        if (!flag) flag = MWF.xApplication.cms.Xform.LP.notValidation;
        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }
        return true;
    }
	
}); 