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
                    if( layout.mobile ){
                        var okAction = this.dlg.node.getElement(".MWF_dialod_Action_ok");
                        if (okAction) okAction.hide();
                    }else{
                        var okNode = this.dlg.button.getFirst();
                        if(okNode){
                            okNode.hide();
                            var cancelButton = okNode.getNext();
                            if(cancelButton)cancelButton.set("value", o2.LP.widget.close);
                        }
                    }
                    if( (json.data.failureList && json.data.failureList.length) || (json.data.successList && json.data.successList.length)  ){
                        this.showCreateResult(json.data.failureList, json.data.successList);
                    }
                    this.loadAssociatedDocument(function () {
                        this.fireEvent("afterSelectResult", [this.documentList]);
                    }.bind(this));
                }.bind(this));
            }else{
                this.status = "showResult";
                this.loadAssociatedDocument(function () {
                    this.fireEvent("afterSelectResult", [this.documentList]);
                }.bind(this));
                if( this.dlg )this.dlg.close();
            }
        }.bind(this));
	},
	cancelAllAssociated: function( callback ){
		var _self = this;
		if( this.documentList.length ){
			var ids = [];
            if( this.json.reserve === false ){
                ids = this.documentList.map(function (doc) {
                    return doc.id;
                });
            }else{
                var viewIds = (this.json.queryView || []).map(function (view) {
                   return view.id;
                });
                var docs = this.documentList.filter(function (doc) {
                    return viewIds.contains( doc.view );
                });
                ids = docs.map(function (doc) {
                    return doc.id;
                });
            }
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
	loadAssociatedDocument: function( callback ){
		this.documentListNode.empty();
		o2.Actions.load("x_cms_assemble_control").CorrelationAction.listWithDocumentWithSite(this.form.businessData.document.id, (this.json.site || this.json.id), function (json) {
			this.documentList = json.data;
			this.showDocumentList();
            if(callback)callback();
		}.bind(this));
	},
	cancelAssociated: function(e, d, itemNode){
		var lp = MWF.xApplication.cms.Xform.LP;
		var _self = this;
		this.form.confirm("warn", e, lp.cancelAssociatedTitle, lp.cancelAssociated.replace("{title}", o2.txt(d.targetTitle)), 370, 120, function () {
			_self.fireEvent("deleteDocument", [d]);
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