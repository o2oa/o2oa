MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Label = MWF.APPLabel =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Module,
	
	_loadUserInterface: function(){
		if (this.json.valueType == "text"){
			this.node.set("text", this.json.text || "");
		}
		if (this.json.valueType == "script"){
			var code = this.json.script.code;
			if (code){
				this.node.set("text", this.form.Macro.exec(code, this) || "");
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
    setText: function(text){
        this.node.set("text", text);
    }
});