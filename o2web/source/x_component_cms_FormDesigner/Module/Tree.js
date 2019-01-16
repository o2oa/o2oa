MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Tree", null, false);
MWF.xApplication.cms.FormDesigner.Module.Tree = MWF.CMSFCTree = new Class({
	Extends: MWF.FCTree,
	Implements : [MWF.CMSFCMI]
});
