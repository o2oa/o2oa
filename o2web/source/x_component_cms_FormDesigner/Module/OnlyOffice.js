MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OnlyOffice", null, false);
MWF.xApplication.cms.FormDesigner.Module.OnlyOffice = MWF.CMSFCOnlyOffice = new Class({
	Extends: MWF.FCOnlyOffice,
	Implements : [MWF.CMSFCMI]
});
