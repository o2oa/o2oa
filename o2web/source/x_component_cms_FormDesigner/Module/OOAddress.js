MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOAddress", null, false);
MWF.xApplication.cms.FormDesigner.Module.OOAddress = MWF.CMSFCOOAddress = new Class({
	Extends: MWF.FCOOAddress,
	Implements : [MWF.CMSFCMI]
});
