MWF.xDesktop.requireApp("process.Xform", "OnlyOffice", null, false);
MWF.xApplication.cms.Xform.OnlyOffice = MWF.CMSOnlyOffice =  new Class({
    Extends: MWF.APPOnlyOffice,
    createDocument : function (callback){
        var data = {
            "fileName" : "文件正文." + this.json.officeType,
            "fileType" : this.json.officeType,
            "relevanceId" : this.form.businessData.document.id
        }
        this.action.OnlyofficeAction.create(data,
            function( json ){
                this.documentId = json.data.id;
                this.setData();
                if (callback) callback();
            }.bind(this),null, false
        );
    }
});