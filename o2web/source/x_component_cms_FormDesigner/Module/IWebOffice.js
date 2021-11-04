MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.IWebOffice", null, false);
MWF.xApplication.cms.FormDesigner.Module.IWebOffice = MWF.CMSFCIWebOffice = new Class({
	Extends: MWF.FCIWebOffice,
	Implements : [MWF.CMSFCMI]
});
