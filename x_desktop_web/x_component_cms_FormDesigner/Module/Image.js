MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Image", null, false);
MWF.xApplication.cms.FormDesigner.Module.Image = MWF.CMSFCImage = new Class({
	Extends: MWF.FCImage,
	Implements : [MWF.CMSFCMI]
});
