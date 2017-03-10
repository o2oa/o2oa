MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Label", null, false);
MWF.xApplication.cms.FormDesigner.Module.Label = MWF.CMSFCLabel = new Class({
	Extends: MWF.FCLabel,
	Implements : [MWF.CMSFCMI]
});
