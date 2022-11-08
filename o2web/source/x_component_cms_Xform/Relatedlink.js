MWF.xDesktop.requireApp("process.Xform", "Relatedlink", null, false);
MWF.xApplication.cms.Xform.Relatedlink = MWF.CMSRelatedlink =  new Class({
	Extends: MWF.APPRelatedlink,
	getFlag: function(){
		return this.form.businessData.document.id;
	},
	getCategory: function(){
		return "cms";
	}
}); 