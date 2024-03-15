MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Security", null, false);
MWF.xApplication.cms.FormDesigner.Module.Security = MWF.CMSFCSecurity = new Class({
	Extends: MWF.FCSecurity,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default"
	}
});
