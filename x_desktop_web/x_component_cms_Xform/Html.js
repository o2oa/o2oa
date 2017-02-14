MWF.xDesktop.requireApp("cms.Xform", "$Module", null, false);
MWF.xApplication.cms.Xform.Html = MWF.CMSHtml =  new Class({
	Extends: MWF.CMS$Module,

	load: function(){
		this.node.appendHTML(this.json.text, "after");
		this.node.destory();
	}
});