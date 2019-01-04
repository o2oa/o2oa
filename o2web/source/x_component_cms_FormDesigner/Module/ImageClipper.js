MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.ImageClipper", null, false);
MWF.xApplication.cms.FormDesigner.Module.ImageClipper = MWF.CMSFCImageClipper = new Class({
	Extends: MWF.FCImageClipper,
	Implements : [MWF.CMSFCMI]
});
