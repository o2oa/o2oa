MWF.xDesktop.requireApp("process.Xform", "IWebOffice", null, false);
MWF.xApplication.cms.Xform.IWebOffice = MWF.CMSIWebOffice =  new Class({
    Extends: MWF.APPIWebOffice,
    getFormId: function(){
        var id = this.form.businessData.document.id;
        return "form"+this.json.id+id;
    },
    getOfficeObjectId: function(){
        var id = this.form.businessData.document.id;
        return "WebOffice"+this.json.id+id;
    }
});