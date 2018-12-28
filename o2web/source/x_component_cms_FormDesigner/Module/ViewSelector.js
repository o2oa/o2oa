MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.ViewSelector", null, false);
MWF.xApplication.cms.FormDesigner.Module.ViewSelector = MWF.CMSFCViewSelector = new Class({
	Extends: MWF.FCViewSelector,
	Implements : [MWF.CMSFCMI]
});
