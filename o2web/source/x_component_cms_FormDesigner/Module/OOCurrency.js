MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOCurrency", null, false);
MWF.xApplication.cms.FormDesigner.Module.OOCurrency = MWF.CMSFCOOCurrency = new Class({
	Extends: MWF.FCOOCurrency,
	Implements : [MWF.CMSFCMI]
});
