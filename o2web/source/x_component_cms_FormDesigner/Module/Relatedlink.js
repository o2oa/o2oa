MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Relatedlink", null, false);
MWF.xApplication.cms.FormDesigner.Module.Relatedlink = MWF.CMSFCRelatedlink = new Class({
	Extends: MWF.FCRelatedlink,
	Implements : [MWF.CMSFCMI]
});
