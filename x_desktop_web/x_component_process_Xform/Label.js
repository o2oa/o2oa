MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Label = MWF.APPLabel =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Module,
	
	_loadUserInterface: function(){
		if (this.json.valueType == "text"){
			this.node.set("text", this.json.text);
		}
		if (this.json.valueType == "script"){
			var code = this.json.script.code;
			if (code){
				this.node.set("text", this.form.Macro.exec(code, this));
			} 
		}
	}
});