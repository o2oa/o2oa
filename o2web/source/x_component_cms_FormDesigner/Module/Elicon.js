MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elicon", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elicon = MWF.CMSFCElicon = new Class({
	Extends: MWF.FCElicon,
	Implements : [MWF.CMSFCMI]
});
