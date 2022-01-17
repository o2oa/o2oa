MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Eldropdown", null, false);
MWF.xApplication.cms.FormDesigner.Module.Eldropdown = MWF.CMSFCEldropdown = new Class({
	Extends: MWF.FCEldropdown,
	Implements : [MWF.CMSFCMI]
});
