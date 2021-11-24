MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elswitch", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elswitch = MWF.CMSFCElswitch = new Class({
	Extends: MWF.FCElswitch,
	Implements : [MWF.CMSFCMI]
});
