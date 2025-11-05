MWF.xDesktop.requireApp("process.Xform", "AssociatedDocument", null, false);
MWF.xApplication.cms.Xform.AssociatedDocument = MWF.CMSAssociatedDocument =  new Class({
	Extends: MWF.APPAssociatedDocument,
	getBundle: function(){
		return this.form.businessData.document.id;
	},
    _updateAssociation: function (data, callback, async){
        var result;
        var p = o2.Actions.load("x_cms_assemble_control").CorrelationAction.updateWithDocument(
            this.getBundle(),
            this._parseUpdateDate(data),
            (json)=>{
                result = json;
                if(callback)callback(json);
                return json;
            },
            null,
            async !== false
        );
        return async === false ? result : p;
    },
    _createAssociation: function( data, async ){
        var result;
        var p = o2.Actions.load("x_cms_assemble_control").CorrelationAction.createWithDocument(
            this.getBundle(),
            { targetList: data },
            (json)=>{
                result = json;
                return json;
            },
            null,
            async !== false
        );
        return async === false ? result : p;
    },
    _cancelAssociated: function(ids, async){
        var result;
        var p = o2.Actions.load("x_cms_assemble_control").CorrelationAction.deleteWithDocument(
            this.getBundle(),
            { idList: ids },
            (json)=>{
                result = json;
                return json;
            },
            null,
            async !== false
        );
        return async === false ? result : p;
    },
    _listAllAssociated: function(async, callback){
        var result;
        var p =  o2.Actions.load("x_cms_assemble_control").CorrelationAction.listWithDocumentWithSite(
            this.getBundle(),
            this.json.site || this.json.id,
            (json)=>{
                result = json;
                if(callback)callback(json);
                return json;
            },
            null,
            async !== false
        );
        return async === false ? result : p;
    },
    getLp: function(){
        return MWF.xApplication.cms.Xform.LP;
    }
});
