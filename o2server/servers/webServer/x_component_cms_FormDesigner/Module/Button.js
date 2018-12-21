MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Button", null, false);
MWF.xApplication.cms.FormDesigner.Module.Button = MWF.CMSFCButton = new Class({
	Extends: MWF.FCButton,
	Implements : [MWF.CMSFCMI]
});
