MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOSelect", null, false);
MWF.xApplication.cms.FormDesigner.Module.OOSelect = MWF.CMSFCOOSelect = new Class({
	Extends: MWF.FCOOSelect,
	Implements : [MWF.CMSFCMI]
});
