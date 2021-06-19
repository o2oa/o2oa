MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Importer", null, false);
MWF.xApplication.cms.FormDesigner.Module.Importer = MWF.CMSFCImporter = new Class({
	Extends: MWF.FCImporter,
	Implements : [MWF.CMSFCMI]
});
