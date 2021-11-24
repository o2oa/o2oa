MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elcheckbox", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elcheckbox = MWF.CMSFCElcheckbox = new Class({
	Extends: MWF.FCElcheckbox,
	Implements : [MWF.CMSFCMI]
});
