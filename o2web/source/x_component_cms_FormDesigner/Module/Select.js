MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Select", null, false);
MWF.xApplication.cms.FormDesigner.Module.Select = MWF.CMSFCSelect = new Class({
	Extends: MWF.FCSelect,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default"
	}
});
