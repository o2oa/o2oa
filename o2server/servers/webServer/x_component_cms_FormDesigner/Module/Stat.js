MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Stat", null, false);
MWF.xApplication.cms.FormDesigner.Module.Stat = MWF.CMSFCStat = new Class({
	Extends: MWF.FCStat,
	Implements : [MWF.CMSFCMI]
});