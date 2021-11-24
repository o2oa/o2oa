MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elcascader", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elcascader = MWF.CMSFCElcascader = new Class({
	Extends: MWF.FCElcascader,
	Implements : [MWF.CMSFCMI]
});
