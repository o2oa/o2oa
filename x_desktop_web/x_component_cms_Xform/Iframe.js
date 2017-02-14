MWF.xDesktop.requireApp("cms.Xform", "$Module", null, false);
MWF.xApplication.cms.Xform.Iframe = MWF.CMSIframe =  new Class({
	Extends: MWF.CMS$Module,

	_loadUserInterface: function(){
		this.node.empty();

        var src = this.json.src;
        if (this.json.valueType=="script"){
            src = this.form.CMSMacro.exec(this.json.script.code, this);
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