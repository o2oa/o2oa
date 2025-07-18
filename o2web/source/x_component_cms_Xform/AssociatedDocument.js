MWF.xDesktop.requireApp("process.Xform", "AssociatedDocument", null, false);
MWF.xApplication.cms.Xform.AssociatedDocument = MWF.CMSAssociatedDocument =  new Class({
	Extends: MWF.APPAssociatedDocument,
	getBundle: function(){
		return this.form.businessData.document.id;
	},
    _createAssociation: function( data, async ){
        return o2.Actions.load("x_cms_assemble_control").CorrelationAction.createWithDocument(
            this.getBundle(),
            { targetList: data },
            null,
            null,
            async !== false
        );
    },
    _cancelAssociated: function(ids, async){
        return o2.Actions.load("x_cms_assemble_control").CorrelationAction.deleteWithDocument(
            this.getBundle(),
            { idList: ids },
            null,
            null,
            async !== false
        );
    },
    _listAllAssociated: function(async){
        return o2.Actions.load("x_cms_assemble_control").CorrelationAction.listWithDocumentWithSite(
            this.getBundle(),
            this.json.site || this.json.id,
            null,
            null,
            async !== false
        );
    },
    getLp: function(){
        return MWF.xApplication.cms.Xform.LP;
    }
});
