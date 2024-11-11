MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOButton", null, false);
MWF.xApplication.cms.FormDesigner.Module.OOButton = MWF.CMSFCOOButton = new Class({
	Extends: MWF.FCOOButton,
	Implements : [MWF.CMSFCMI]
});
