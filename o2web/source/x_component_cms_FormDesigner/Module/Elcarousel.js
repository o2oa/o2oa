MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elcarousel", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elcarousel = MWF.CMSFCElcarousel = new Class({
	Extends: MWF.FCElcarousel,
	Implements : [MWF.CMSFCMI]
});
