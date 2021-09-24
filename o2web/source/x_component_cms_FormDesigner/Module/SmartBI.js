MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.SmartBI", null, false);
MWF.xApplication.cms.FormDesigner.Module.SmartBI = MWF.CMSFCSmartBI = new Class({
	Extends: MWF.FCSmartBI,
	Implements: [Options, Events]

});
