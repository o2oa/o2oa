MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Eldatetime", null, false);
MWF.xApplication.cms.FormDesigner.Module.Eldatetime = MWF.CMSFCEldatetime = new Class({
	Extends: MWF.FCEldatetime,
	Implements : [MWF.CMSFCMI]
});
