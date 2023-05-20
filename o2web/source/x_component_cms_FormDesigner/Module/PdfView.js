MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.PdfView", null, false);
MWF.xApplication.cms.FormDesigner.Module.PdfView = MWF.CMSFCPdfView = new Class({
	Extends: MWF.FCPdfView,
	Implements : [MWF.CMSFCMI]
});
