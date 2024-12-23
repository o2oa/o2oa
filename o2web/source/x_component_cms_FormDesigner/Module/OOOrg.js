MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOOrg", null, false);
MWF.xApplication.cms.FormDesigner.Module.OOOrg = MWF.CMSFCOOOrg = new Class({
	Extends: MWF.FCOOOrg,
	Implements : [MWF.CMSFCMI]
});
