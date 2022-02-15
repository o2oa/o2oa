MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Eltree", null, false);
MWF.xApplication.cms.FormDesigner.Module.Eltree = MWF.CMSFCEltree = new Class({
	Extends: MWF.FCEltree,
	Implements : [MWF.CMSFCMI]
});
