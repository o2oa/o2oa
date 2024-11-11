MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOCheckGroup", null, false);
MWF.xApplication.cms.FormDesigner.Module.OOCheckGroup = MWF.CMSFCOOCheckGroup = new Class({
	Extends: MWF.FCOOCheckGroup,
	Implements : [MWF.CMSFCMI]
});
