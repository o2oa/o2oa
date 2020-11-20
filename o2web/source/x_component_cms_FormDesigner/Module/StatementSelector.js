MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.StatementSelector", null, false);
MWF.xApplication.cms.FormDesigner.Module.StatementSelector = MWF.CMSFCStatementSelector = new Class({
	Extends: MWF.FCStatementSelector,
	Implements : [MWF.CMSFCMI]
});
