MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Iframe", null, false);
MWF.xApplication.cms.FormDesigner.Module.Iframe = MWF.CMSFCIframe = new Class({
	Extends: MWF.FCIframe,
	Implements : [MWF.CMSFCMI]
});
