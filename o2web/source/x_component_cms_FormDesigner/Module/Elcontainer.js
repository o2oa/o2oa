MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elcontainer$Container", null, false);
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elcontainer", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elcontainer = MWF.CMSFCElcontainer = new Class({
	Extends: MWF.FCElcontainer,
	Implements : [MWF.CMSFCMI]
});
