MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Orgfield", null, false);
MWF.xApplication.cms.FormDesigner.Module.Orgfield = MWF.CMSFCOrgfield = new Class({
	Extends: MWF.FCOrgfield,
	Implements : [MWF.CMSFCMI]
});
