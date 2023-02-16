MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OfficeOnline", null, false);
MWF.xApplication.cms.FormDesigner.Module.OfficeOnline = MWF.CMSFCOfficeOnline = new Class({
	Extends: MWF.FCOfficeOnline,
	Implements : [MWF.CMSFCMI]
});
