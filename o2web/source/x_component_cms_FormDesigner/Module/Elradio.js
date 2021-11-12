MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elradio", null, false);
MWF.xApplication.cms.FormDesigner.Module.Elradio = MWF.CMSFCElradio = new Class({
	Extends: MWF.FCElradio,
	Implements : [MWF.CMSFCMI]
});
