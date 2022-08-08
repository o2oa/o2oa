MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.WpsOffice2", null, false);
MWF.xApplication.cms.FormDesigner.Module.WpsOffice2 = MWF.CMSFCWpsOffice2 = new Class({
	Extends: MWF.FCWpsOffice,
	Implements : [MWF.CMSFCMI]
});
