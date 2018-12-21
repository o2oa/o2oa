MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Iframe = MWF.APPIframe =  new Class({
	Extends: MWF.APP$Module,

	_loadUserInterface: function(){
		this.node.empty();

        var src = this.json.src;
        if (this.json.valueType=="script"){
            src = this.form.Macro.exec(this.json.script.code, this);
        }

		this.iframe = new Element("iframe", {
			"src": src
		}).inject(this.node, "after");
		
		this.node.destroy();
		this.node = this.iframe.setStyles({
			"width": "100%",
			"border": "0"
		});
	}
}); 