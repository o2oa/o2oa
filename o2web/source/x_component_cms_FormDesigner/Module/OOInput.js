MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOInput", null, false);
MWF.xApplication.cms.FormDesigner.Module.OOInput = MWF.CMSFCOOInput = new Class({
	Extends: MWF.FCOOInput,
	Implements : [MWF.CMSFCMI]
});
