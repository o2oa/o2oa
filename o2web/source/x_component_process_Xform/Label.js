MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Label = MWF.APPLabel =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Module,
	
	_loadUserInterface: function(){
		if (this.json.valueType == "text"){
			this.node.set("text", this.json.text || "");
		}
		if (this.json.valueType == "script"){
			var code = (this.json.script) ? this.json.script.code : "";
			if (code){
			    var value = this.form.Macro.exec(code, this);
			    this._setNodeText(value);
				//this.node.set("text", this.form.Macro.exec(code, this) || "");
			} 
		}
		if (this.json.prefixIcon || this.json.suffixIcon){
            var text = this.node.get("text");
            this.node.empty();

            var tNode = new Element("div", {"styles": {
            	"margin-left": (this.json.prefixIcon) ? "20px" : "0px",
                "margin-right": (this.json.suffixIcon) ? "20px" : "0px",
                "height": "100%"
			}, "text": text}).inject(this.node);

            if (this.json.prefixIcon){
                var node = new Element("div", {"styles": {
                    "float": "left",
                    "width": "20px",
                    "height": ""+this.node.getSize().y+"px",
                    "background": "url("+this.json.prefixIcon+") center center no-repeat"
                }}).inject(tNode, "before");
            }
            if (this.json.suffixIcon){
                var node = new Element("div", {"styles": {
                    "float": "right",
                    "width": "20px",
                    "height": ""+this.node.getSize().y+"px",
                    "background": "url("+this.json.suffixIcon+") center center no-repeat"
                }}).inject(tNode, "before");
            }
		}
	},
    _setNodeText: function(value){
        if (value && value.isAG){
            value.addResolve(function(v){
                this._setNodeText(v);
            }.bind(this));
        }else{
            o2.promiseAll(value).then(function(v){
                this.node.set("text", v || "");
            }.bind(this), function(){});
            //this.node.set("text", value || "");
        }
    },
    setText: function(text){
	    if (!!text){
            o2.promiseAll(text).then(function(v){
                this.node.set("text", v || "");
            }.bind(this), function(){});
        }else{
            this.node.set("text", v || "");
        }
        //this.node.set("text", text);
    }
});
