MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elnumber", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elnumber = MWF.CMSFCElnumber = new Class({
	Extends: MWF.FCElnumber,
	Implements : [MWF.CMSFCMI]
});
