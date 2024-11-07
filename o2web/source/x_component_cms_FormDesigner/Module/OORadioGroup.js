MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OORadioGroup", null, false);
MWF.xApplication.cms.FormDesigner.Module.OORadioGroup = MWF.CMSFCOORadioGroup = new Class({
	Extends: MWF.FCOORadioGroup,
	Implements : [MWF.CMSFCMI]
});
