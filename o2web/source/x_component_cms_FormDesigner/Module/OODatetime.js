MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OODatetime", null, false);
MWF.xApplication.cms.FormDesigner.Module.OODatetime = MWF.CMSFCOODatetime = new Class({
	Extends: MWF.FCOODatetime,
	Implements : [MWF.CMSFCMI]
});
