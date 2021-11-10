MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.YozoOffice", null, false);
MWF.xApplication.cms.FormDesigner.Module.YozoOffice = MWF.CMSFCYozoOffice = new Class({
	Extends: MWF.FCYozoOffice,
	Implements : [MWF.CMSFCMI]
});
