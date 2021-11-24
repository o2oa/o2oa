MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elselect", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elselect = MWF.CMSFCElselect = new Class({
	Extends: MWF.FCElselect,
	Implements : [MWF.CMSFCMI]
});
