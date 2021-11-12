MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elautocomplete", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elautocomplete = MWF.CMSFCElautocomplete = new Class({
	Extends: MWF.FCElautocomplete,
	Implements : [MWF.CMSFCMI]
});
