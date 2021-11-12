MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elslider", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elslider = MWF.CMSFCElswitch = new Class({
	Extends: MWF.FCElslider,
	Implements : [MWF.CMSFCMI]
});
