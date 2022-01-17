MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elcolorpicker", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elcolorpicker = MWF.CMSFCElcolorpicker = new Class({
	Extends: MWF.FCElcolorpicker,
	Implements : [MWF.CMSFCMI]
});
