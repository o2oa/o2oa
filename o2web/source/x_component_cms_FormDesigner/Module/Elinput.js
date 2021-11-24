MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elinput", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elinput = MWF.CMSFCElinput = new Class({
	Extends: MWF.FCElinput,
	Implements : [MWF.CMSFCMI]
});
