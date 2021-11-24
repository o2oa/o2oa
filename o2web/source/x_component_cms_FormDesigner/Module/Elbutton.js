MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elbutton", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elbutton = MWF.CMSFCElbutton = new Class({
	Extends: MWF.FCElbutton,
	Implements : [MWF.CMSFCMI]
});
