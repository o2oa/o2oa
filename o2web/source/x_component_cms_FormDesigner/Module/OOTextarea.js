MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOTextarea", null, false);
MWF.xApplication.cms.FormDesigner.Module.OOTextarea = MWF.CMSFCOOTextarea = new Class({
	Extends: MWF.FCOOTextarea,
	Implements : [MWF.CMSFCMI]
});
