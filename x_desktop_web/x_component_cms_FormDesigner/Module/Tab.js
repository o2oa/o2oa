MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Tab", null, false);
MWF.xApplication.cms.FormDesigner.Module.Tab = MWF.CMSFCTab = new Class({
	Extends: MWF.FCTab,
	Implements : [MWF.CMSFCMI]
});