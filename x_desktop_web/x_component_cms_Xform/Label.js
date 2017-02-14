MWF.xDesktop.requireApp("cms.Xform", "$Module", null, false);
MWF.xApplication.cms.Xform.Label = MWF.CMSLabel =  new Class({
	Implements: [Events],
	Extends: MWF.CMS$Module,
	
	_loadUserInterface: function(){
		if (this.json.valueType == "text"){
			this.node.set("text", this.json.text);
		}
		if (this.json.valueType == "script"){
			var code = this.json.script.code;
			if (code){
				this.node.set("text", this.form.CMSMacro.exec(code, this));
			} 
		}
	}
});