MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Documenteditor", null, false);
MWF.xDesktop.requireApp("cms.FormDesigner", "Property", null, false);
MWF.xApplication.cms.FormDesigner.Module.Documenteditor = MWF.CMSFCDocumenteditor = new Class({
	Extends: MWF.FCDocumenteditor,
	Implements : [MWF.CMSFCMI]
});