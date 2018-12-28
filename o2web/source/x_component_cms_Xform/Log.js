MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("cms.Xform", "widget.Log", null, false);
MWF.xApplication.cms.Xform.Log = MWF.CMSLog =  new Class({
	Extends: MWF.APP$Module,
	_loadUserInterface: function(){
		this.node.empty();
        this.node.setStyle("-webkit-user-select", "text");
        this.log = new MWF.xApplication.cms.Xform.widget.Log( this.form.app, this.node, {
            "documentId" : this.form.businessData.document.id,
            "mode" : this.json.mode,
            "textStyle" : this.json.textStyle
        });
        this.log.load();
	}
}); 