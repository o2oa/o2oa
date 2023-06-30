MWF.xDesktop.requireApp("process.Xform", "AssociatedDocument", null, false);
MWF.xApplication.cms.Xform.AssociatedDocument = MWF.CMSAssociatedDocument =  new Class({
	Extends: MWF.APPAssociatedDocument,
	getBundle: function(){
		return this.form.businessData.document.id;
	},
	selectDocument: function(data){
		o2.Actions.load("x_cms_assemble_control").CorrelationAction.createWithDocument(this.form.businessData.document.id, {
			targetList: data
		}, function (json) {
			this.loadAssociatedDocument();
		}.bind(this));
	},
	loadAssociatedDocument: function(){
		this.documentListNode.empty();
		o2.Actions.load("x_cms_assemble_control").CorrelationAction.listWithDocumentWithSite(this.form.businessData.document.id, (this.json.site || this.json.id), function (json) {
			this.documentList = json.data;
			this.showDocumentList();
		}.bind(this));
	},
	cancelAssociated: function(e, d, itemNode){
		var lp = MWF.xApplication.cms.Xform.LP;
		var _self = this;
		this.form.confirm("warn", e, lp.cancelAssociatedTitle, lp.cancelAssociated.replace("{title}", o2.txt(d.targetTitle)), 370, 120, function () {
			o2.Actions.load("x_cms_assemble_control").CorrelationAction.deleteWithDocument(_self.form.businessData.document.id, {
				idList: [d.id]
			},function (json) {
				itemNode.destroy();
				_self.documentList.erase(d);
				this.close();
				//this.showDocumentList();
			}.bind(this));
		}, function () {
			this.close();
		});
	}
}); 