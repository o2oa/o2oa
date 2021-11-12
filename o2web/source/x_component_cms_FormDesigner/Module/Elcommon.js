MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elcommon", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elcommon = MWF.CMSFCElcommon = new Class({
	Extends: MWF.FCElcommon,
	Implements : [MWF.CMSFCMI]
});
