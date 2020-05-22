MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Org", null, false);
MWF.xApplication.cms.FormDesigner.Module.Org = MWF.CMSFCOrg = new Class({
	Extends: MWF.FCOrg,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Org/org.html"
	}
});
