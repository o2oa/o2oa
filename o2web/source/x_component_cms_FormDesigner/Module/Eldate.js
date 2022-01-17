MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Eldate", null, false);
MWF.xApplication.cms.FormDesigner.Module.Eldate = MWF.CMSFCEldate = new Class({
	Extends: MWF.FCEldate,
	Implements : [MWF.CMSFCMI]
});
