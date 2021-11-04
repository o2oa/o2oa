MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.WpsOffice", null, false);
MWF.xApplication.cms.FormDesigner.Module.WpsOffice = MWF.CMSFCWpsOffice = new Class({
	Extends: MWF.FCWpsOffice,
	Implements : [MWF.CMSFCMI]
});
