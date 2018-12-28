MWF.xDesktop.requireApp("process.Xform", "Tab", null, false);
MWF.xApplication.cms.Xform.Tab = MWF.CMSTab =  new Class({
	Extends: MWF.APPTab
});

MWF.xApplication.cms.Xform.tab$Page = MWF.CMSTab$Page = new Class({
	Extends: MWF.APPTab$Page
});
MWF.xApplication.cms.Xform.tab$Content = MWF.CMSTab$Content = new Class({
	Extends: MWF.APPTab$Content
});