MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Eltime", null, false);
MWF.xApplication.cms.FormDesigner.Module.Eltime = MWF.CMSFCEltime = new Class({
	Extends: MWF.FCEltime,
	Implements : [MWF.CMSFCMI]
});
