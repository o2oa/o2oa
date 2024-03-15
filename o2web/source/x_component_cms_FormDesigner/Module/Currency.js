MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Currency", null, false);
MWF.xApplication.cms.FormDesigner.Module.Currency = MWF.CMSFCCurrency = new Class({
	Extends: MWF.FCCurrency,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default"
	}
});
