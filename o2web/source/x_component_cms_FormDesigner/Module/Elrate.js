MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elrate", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elrate = MWF.CMSFCElrate = new Class({
	Extends: MWF.FCElrate,
	Implements : [MWF.CMSFCMI]
});
