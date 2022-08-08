MWF.xDesktop.requireApp("process.Xform", "WpsOffice2", null, false);
MWF.xApplication.cms.Xform.WpsOffice2 = MWF.CMSWpsOffice2 =  new Class({
    Extends: MWF.APPWpsOffice2,
    createDocument : function (callback){
        var json = {};
        if(this.json.template !== ""){
            json.templateId = this.json.template;
            this.json.officeType = "word";
        }
        if(this.json.fileName !== ""){
            json.fileName = this.json.fileName;
        }
        json.category = "document";

        json.docId = this.form.businessData.document.id;

        this.action.CustomAction.createFileBlank(this.json.officeType,json, function( json ){
            this.documentId = json.data.docId;
            this.setData();
            if (callback) callback();
        }.bind(this),null, false);
    },
});
