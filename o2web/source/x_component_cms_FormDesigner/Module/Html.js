MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Html", null, false);
MWF.xApplication.cms.FormDesigner.Module.Html = MWF.CMSFCHtml = new Class({
	Extends: MWF.FCHtml,
	Implements : [MWF.CMSFCMI]
});
