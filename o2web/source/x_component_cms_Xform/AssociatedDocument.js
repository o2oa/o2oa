MWF.xDesktop.requireApp("process.Xform", "AssociatedDocument", null, false);
MWF.xApplication.cms.Xform.AssociatedDocument = MWF.CMSAssociatedDocument =  new Class({
	Extends: MWF.APPAssociatedDocument,
	getBundle: function(){
		return this.form.businessData.document.id;
	},
	selectDocument: function(data){
        this.cancelAllAssociated( function () {
            if( data && data.length ){
                o2.Actions.load("x_cms_assemble_control").CorrelationAction.createWithDocument(this.form.businessData.document.id, {
                    targetList: data
                }, function (json) {
                    this.status = "showResult";
                    if(this.dlg.titleText)this.dlg.titleText.set("text", MWF.xApplication.process.Xform.LP.associatedResult);
                    var okNode = this.dlg.button.getFirst();
                    if(okNode){
                        okNode.hide();
                        var cancelButton = okNode.getNext();
                        if(cancelButton)cancelButton.set("value", o2.LP.widget.close);
                    }
                    if( (json.data.failureList && json.data.failureList.length) || (json.data.successList && json.data.successList.length)  ){
                        this.showCreateResult(json.data.failureList, json.data.successList);
                    }
                    this.loadAssociatedDocument();
                }.bind(this));
            }else{
                this.status = "showResult";
                this.loadAssociatedDocument();
                if( this.dlg )this.dlg.close();
            }
        }.bind(this));
	},
	cancelAllAssociated: function( callback ){
		var _self = this;
		if( this.documentList.length ){
			var ids = this.documentList.map(function (doc) {
				return doc.id;
			});
			o2.Actions.load("x_cms_assemble_control").CorrelationAction.deleteWithDocument(this.getBundle(), {
				idList: ids
			},function (json) {
				this.documentList = [];
				if(callback)callback();
			}.bind(this));
		}else{
			if(callback)callback();
		}
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