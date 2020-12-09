MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("cms.Xform", "widget.Comment", null, false);
MWF.xApplication.cms.Xform.Comment = MWF.CMSComment =  new Class({
	Extends: MWF.APP$Module,
	_loadUserInterface: function(){
		this.node.empty();
        this.node.setStyle("-webkit-user-select", "text");

        debugger;

        var config = {};
        if(this.json.editorProperties){
            config = Object.clone(this.json.editorProperties);
        }
        if (this.json.config){
            if (this.json.config.code){
                var obj = this.form.Macro.exec(this.json.config.code, this);
                Object.each(obj, function(v, k){
                    config[k] = v;
                });
            }
        }

        this.comment = new MWF.xApplication.cms.Xform.widget.Comment( this.form.app, this.node, {
            "documentId" : this.form.businessData.document.id,
            "countPerPage" : this.json.countPerPage || 10,
            "isAllowModified" : this.json.isAllowModified,
            "isAllowPublish" : this.json.isAllowPublish,
            "isAdmin" : this.form.app.isAdmin,
            "editorProperties" : config
        });
        this.comment.load();
	}
}); 