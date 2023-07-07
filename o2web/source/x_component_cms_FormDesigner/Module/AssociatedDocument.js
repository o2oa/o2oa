MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.AssociatedDocument", null, false);
MWF.xApplication.cms.FormDesigner.Module.AssociatedDocument = MWF.CMSFCAssociatedDocument = new Class({
	Extends: MWF.FCAssociatedDocument,
	Implements : [MWF.CMSFCMI]
});
