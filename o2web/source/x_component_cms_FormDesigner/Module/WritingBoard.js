MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.WritingBoard", null, false);
MWF.xApplication.cms.FormDesigner.Module.WritingBoard = MWF.CMSFCWritingBoard = new Class({
	Extends: MWF.FCWritingBoard,
	Implements : [MWF.CMSFCMI]
});
