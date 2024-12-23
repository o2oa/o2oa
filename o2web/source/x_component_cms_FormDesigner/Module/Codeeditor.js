MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Codeeditor", null, false);
MWF.xApplication.cms.FormDesigner.Module.Codeeditor = MWF.CMSFCCodeeditor = new Class({
	Extends: MWF.FCCodeeditor,
	Implements : [MWF.CMSFCMI]
});
