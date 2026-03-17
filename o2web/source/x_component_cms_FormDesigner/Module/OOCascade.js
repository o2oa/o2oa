MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOCascade", null, false);
MWF.xApplication.cms.FormDesigner.Module.OOCascade = MWF.CMSFCOOCascade = new Class({
	Extends: MWF.FCOOCascade,
	Implements : [MWF.CMSFCMI]
});
