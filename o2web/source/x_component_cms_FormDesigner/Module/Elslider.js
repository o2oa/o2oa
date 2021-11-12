MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elslider", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elslider = MWF.CMSFCElslider = new Class({
	Extends: MWF.FCElslider,
	Implements : [MWF.CMSFCMI]
});
