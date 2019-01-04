MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Office", null, false);
MWF.xApplication.cms.FormDesigner.Module.Office = MWF.CMSFCOffice = new Class({
	Extends: MWF.FCOffice,
	Implements : [MWF.CMSFCMI]
});
