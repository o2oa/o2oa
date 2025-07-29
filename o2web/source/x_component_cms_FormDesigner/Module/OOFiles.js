MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOFiles", null, false);
MWF.xApplication.cms.FormDesigner.Module.OOFiles = MWF.CMSFCOOFiles = new Class({
	Extends: MWF.FCOOFiles,
	Implements : [MWF.CMSFCMI]
});
