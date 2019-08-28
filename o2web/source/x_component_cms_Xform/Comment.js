MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("cms.Xform", "widget.Comment", null, false);
MWF.xApplication.cms.Xform.Comment = MWF.CMSComment =  new Class({
	Extends: MWF.APP$Module,
	_loadUserInterface: function(){
		this.node.empty();
        this.node.setStyle("-webkit-user-select", "text");
        this.comment = new MWF.xApplication.cms.Xform.widget.Comment( this.form.app, this.node, {
            "documentId" : this.form.businessData.document.id,
            "countPerPage" : this.json.countPerPage || 10,
            "isAllowModified" : this.json.isAllowModified,
            "isAdmin" : this.form.app.isAdmin
        });
        this.comment.load();
	}
}); 